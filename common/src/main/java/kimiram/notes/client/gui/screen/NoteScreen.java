package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.gui.widget.ImageWidget;
import kimiram.notes.item.component.NoteContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;

public class NoteScreen extends Screen {
    public int X_OFFSET;
    public final int Y_OFFSET = 20;

    protected final ItemStack stack;
    protected DataComponentType<NoteContent> NOTE_COMPONENT_TYPE;
    protected String text = "";
    protected List<Image> images = new ArrayList<>();

    private final List<ImageWidget> imageWidgets = new ArrayList<>();

    public NoteScreen(ItemStack stack, DataComponentType<NoteContent> componentType) {
        super(Component.literal("Note Screen"));

        this.stack = stack;
        NOTE_COMPONENT_TYPE = componentType;

        NoteContent noteContent = stack.get(NOTE_COMPONENT_TYPE);
        if (noteContent != null) {
            text = noteContent.text();
            images = noteContent.images();
        }
    }

    protected void updateImages() {
        images = new ArrayList<>();
        for (ImageWidget widget: imageWidgets) {
            if (widget.visible && widget.active) {
                images.add(new Image(widget.getImageUrl(),
                        widget.getX() - X_OFFSET, widget.getY() - Y_OFFSET,
                        widget.getWidth(), widget.getHeight()));
            }
        }
    }

    protected void saveNote() {
        updateImages();
        NoteContent noteContent = new NoteContent(text, images);
        stack.set(NOTE_COMPONENT_TYPE, noteContent);
    }

    protected void finalizeNote() {
    }

    private void openAddImageScreen() {
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

        MultiLineEditBox editBox = MultiLineEditBox.builder()
                .setShowDecorations(false)
                .setTextColor(-16777216)
                .setCursorColor(-16777216)
                .setShowBackground(false)
                .setTextShadow(false)
                .setX(X_OFFSET - 4)
                .setY(Y_OFFSET - 4)
                .build(font, 128, 168, CommonComponents.EMPTY);
        editBox.setCharacterLimit(1024);
        editBox.setLineLimit(160 / 9);
        editBox.setValueListener(newText -> text = newText);
        editBox.setValue(text);
        addRenderableWidget(editBox);

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
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        for (int i = imageWidgets.size() - 1; i >= 0; i--) {
            ImageWidget widget = imageWidgets.get(i);
            if (widget.visible && widget.active) {
                widget.renderImage(graphics);
            }
        }
        boolean changedCursor = false;
        for (ImageWidget widget: imageWidgets) {
            if (widget.visible && widget.active) {
                if (!changedCursor) {
                    changedCursor = widget.changeCursor(graphics, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/note.png"),
                (width - 128) / 2 - 16, 12, 0, 0,
                256, 256, 256, 256, 256, 256);
    }
}
