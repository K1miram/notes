package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.util.ImageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AddImageScreen extends Screen {
    private final NoteScreen parent;
    private String imageUrl;

    protected AddImageScreen(NoteScreen parent) {
        super(Component.literal("Add Image Screen"));

        this.parent = parent;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void addImageAndClose() {
        parent.addImage(ImageHelper.getImage(imageUrl));
        onClose();
    }

    private void apply(String url) {
        imageUrl = url;
        ImageHelper.downloadImage(url);
    }

    @Override
    protected void init() {
        EditBox urlField = new EditBox(font,
                width / 2 - 120 - 5, height * 2 / 3, 180, 20, Component.empty());
        urlField.setMaxLength(512);
        addRenderableWidget(urlField);

        Button applyButton = Button.builder(Component.translatable("gui.notes.apply"),
                button -> apply(urlField.getValue())).build();
        applyButton.setRectangle(60, 20, width / 2 + 65, height * 2 / 3);
        addRenderableWidget(applyButton);

        Button doneButton = Button.builder(Component.translatable("gui.done"),
                button -> addImageAndClose()).build();
        doneButton.setRectangle(120, 20, width / 2 + 5, height * 2 / 3 + 30);
        addRenderableWidget(doneButton);

        Button cancelButton = Button.builder(Component.translatable("gui.cancel"), button -> onClose()).build();
        cancelButton.setRectangle(120, 20, (width) / 2 - 120 - 5, height * 2 / 3 + 30);
        addRenderableWidget(cancelButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        ResourceLocation id = ImageHelper.getImageID(imageUrl);
        Image image = ImageHelper.getImage(imageUrl);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id,
                image.x() + (width - 128) / 2 + 4, image.y() + 20, 0, 0,
                image.width(), image.height(), image.width(), image.height());
    }
}
