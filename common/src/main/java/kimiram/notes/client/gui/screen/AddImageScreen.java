package kimiram.notes.client.gui.screen;

import kimiram.imagelib.ImageLib;
import kimiram.notes.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.client.ClientConstants.imageHelper;

public class AddImageScreen extends Screen {
    private final NoteScreen parent;
    private String imageUrl;
    private ImageLib.Type imageType = ImageLib.Type.STATIC_IMAGE;

    protected AddImageScreen(NoteScreen parent) {
        super(Component.literal("Add Image Screen"));

        this.parent = parent;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void addImageAndClose() {
        ImageLib.Size size = imageHelper.fitImageSize(imageUrl, imageType, 120, 160);
        Image newImage = new Image(imageUrl, imageType, 0, 0, size.width(), size.height());
        parent.addImage(newImage);
        onClose();
    }

    private void apply(String url) {
        imageUrl = url;
        imageHelper.downloadImage(url, imageType);
    }

    @Override
    protected void init() {
        EditBox urlField = new EditBox(font,
                width / 2 - 120 - 5, height * 2 / 3 - 10, 250, 20, Component.empty());
        urlField.setMaxLength(512);
        addRenderableWidget(urlField);

        Button typeButton = Button.builder(Component.literal("Static Image"), button -> {
            if (imageType == ImageLib.Type.STATIC_IMAGE) {
                button.setMessage(Component.literal("Gif"));
                imageType = ImageLib.Type.GIF;
            } else {
                button.setMessage(Component.literal("Static Image"));
                imageType = ImageLib.Type.STATIC_IMAGE;
            }
        }).build();
        typeButton.setRectangle(120, 20, width / 2 - 120 - 5, height * 2 / 3 + 20);
        addRenderableWidget(typeButton);

        Button applyButton = Button.builder(Component.translatable("gui.notes.apply"),
                button -> apply(urlField.getValue())).build();
        applyButton.setRectangle(120, 20, width / 2 + 5, height * 2 / 3 + 20);
        addRenderableWidget(applyButton);

        Button doneButton = Button.builder(Component.translatable("gui.done"),
                button -> addImageAndClose()).build();
        doneButton.setRectangle(120, 20, width / 2 + 5, height * 2 / 3 + 50);
        addRenderableWidget(doneButton);

        Button cancelButton = Button.builder(Component.translatable("gui.cancel"), button -> onClose()).build();
        cancelButton.setRectangle(120, 20, width / 2 - 120 - 5, height * 2 / 3 + 50);
        addRenderableWidget(cancelButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        ResourceLocation id = imageHelper.getImageId(imageUrl, imageType, false);
        ImageLib.Size size = imageHelper.fitImageSize(imageUrl, imageType, 120, 160);
        guiGraphics.blit(
                id,
                (width - 128) / 2 + 4, 20, size.width(), size.height(),
                0, 0, size.width(), size.height(), size.width(), size.height()
        );
    }
}
