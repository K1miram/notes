package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.gui.cursor.StandardCursors;
import kimiram.notes.client.gui.widget.ImageWidget;
import kimiram.notes.client.gui.widget.TextFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;

public class NoteScreen extends Screen {
    public int X_OFFSET;
    public final int Y_OFFSET = 20;

    protected ItemStack stack;
    protected String text;
    protected final List<Image> images = new ArrayList<>();

    private TextFieldWidget textFieldWidget;
    private final List<ImageWidget> imageWidgets = new ArrayList<>();

    public NoteScreen(ItemStack stack) {
        super(Component.literal("Note Screen"));

        this.stack = stack;

        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag noteContent = tag.getCompound("note_content");
            text = noteContent.getString("text");

            ListTag imagesTag = noteContent.getList("images", Tag.TAG_COMPOUND);
            for (Tag tag1: imagesTag) {
                CompoundTag imageTag = (CompoundTag) tag1;
                String url = imageTag.getString("url");
                int x = imageTag.getInt("x");
                int y = imageTag.getInt("y");
                int width = imageTag.getInt("width");
                int height = imageTag.getInt("height");
                images.add(new Image(url, x, y, width, height));
            }
        }
    }

    protected void updateImages() {
        images.clear();
        for (ImageWidget widget: imageWidgets) {
            if (widget.visible && widget.active) {
                images.add(new Image(widget.getImageUrl(),
                        widget.getX() - X_OFFSET, widget.getY() - Y_OFFSET,
                        widget.getWidth(), widget.getHeight()));
            }
        }
    }

    protected void saveNote() {
        text = textFieldWidget.getText();
        updateImages();

        CompoundTag tag = stack.getTag() != null ? stack.getTag() : new CompoundTag();
        CompoundTag noteContent = new CompoundTag();
        noteContent.put("text", StringTag.valueOf(text));
        ListTag imagesTag = new ListTag();
        for (Image image: images) {
            CompoundTag imageTag = new CompoundTag();
            imageTag.put("url", StringTag.valueOf(image.url() != null ? image.url() : ""));
            imageTag.put("x", IntTag.valueOf(image.x()));
            imageTag.put("y", IntTag.valueOf(image.y()));
            imageTag.put("width", IntTag.valueOf(image.width()));
            imageTag.put("height", IntTag.valueOf(image.height()));
            imagesTag.add(imageTag);
        }
        noteContent.put("images", imagesTag);
        tag.put("note_content", noteContent);
        stack.setTag(tag);
    }

    @Override
    public void onClose() {
        StandardCursors.ARROW.applyTo(Minecraft.getInstance().getWindow());
        Minecraft.getInstance().setScreen(null);
    }

    protected void finalizeNote() {
        StandardCursors.ARROW.applyTo(Minecraft.getInstance().getWindow());
        Minecraft.getInstance().setScreen(null);
    }

    private void openAddImageScreen() {
        text = textFieldWidget.getText();
        updateImages();
        Minecraft.getInstance().setScreen(new AddImageScreen(this));
    }

    public void addImage(Image newImage) {
        images.add(newImage);
        imageWidgets.add(new ImageWidget(newImage.url(),
                newImage.x() + X_OFFSET, newImage.y() + Y_OFFSET,
                newImage.width(), newImage.height(),
                X_OFFSET, X_OFFSET + 120,
                Y_OFFSET, Y_OFFSET + 160));
        repositionElements();
    }

    @Override
    protected void rebuildWidgets() {
        text = textFieldWidget.getText();
        updateImages();
        imageWidgets.clear();
        super.rebuildWidgets();
    }

    @Override
    protected void init() {
        X_OFFSET = (width - 128) / 2 + 4;

        for (Image image: images) {
            ImageWidget imageWidget = new ImageWidget(image.url(),
                    image.x() + X_OFFSET, image.y() + Y_OFFSET,
                    image.width(), image.height(),
                    X_OFFSET, X_OFFSET + 120,
                    Y_OFFSET, Y_OFFSET + 160);
            imageWidgets.add(imageWidget);
            addRenderableWidget(imageWidget);
        }

        textFieldWidget = new TextFieldWidget(font, X_OFFSET, Y_OFFSET, 120, 160, text);
        addRenderableWidget(textFieldWidget);

        Button addImageButton = new Button.Builder(
                Component.translatable("gui.notes.add_image"), button -> openAddImageScreen()).build();
        addImageButton.setRectangle(170, 20, (width) / 2 - 80 - 5, 190);
        addRenderableWidget(addImageButton);

        Button finalizeNoteButton = new Button.Builder(
                Component.translatable("gui.notes.finalize"), button -> finalizeNote()).build();
        finalizeNoteButton.setRectangle(80, 20, (width) / 2 - 80 - 5, 215);
        addRenderableWidget(finalizeNoteButton);

        Button doneButton = new Button.Builder(
                Component.translatable("gui.done"), button -> onClose()).build();
        doneButton.setRectangle(80, 20, (width) / 2 + 5, 215);
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (int i = imageWidgets.size() - 1; i >= 0; i--) {
            ImageWidget widget = imageWidgets.get(i);
            if (widget.visible && widget.active) {
                widget.renderImage(guiGraphics);
            }
        }
        boolean changedCursor = false;
        for (ImageWidget widget: imageWidgets) {
            if (widget.visible && widget.active) {
                if (!changedCursor) {
                    changedCursor = widget.changeCursor(mouseX, mouseY);
                }
            }
        }
        if (!changedCursor) {
            textFieldWidget.changeMouseCursor();
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(new ResourceLocation(MOD_ID, "textures/gui/note.png"),
                (width - 128) / 2 - 16, 12, 256, 256,
                0, 0, 256, 256, 256, 256);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setDragging(false);
        List<? extends GuiEventListener> children = children();
        for (GuiEventListener child : children) {
            child.mouseReleased(mouseX, mouseY, button);
        }
        return true;
    }
}
