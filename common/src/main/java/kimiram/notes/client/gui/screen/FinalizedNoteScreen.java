package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.util.ImageHelper;
import kimiram.notes.item.component.FinalizedNoteContent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;

public class FinalizedNoteScreen extends Screen {
    private int X_OFFSET;

    protected DataComponentType<FinalizedNoteContent> FINALIZED_NOTE_COMPONENT_TYPE;
    private final FormattedText text;
    private List<FormattedCharSequence> lines;
    private List<Image> images = new ArrayList<>();

    public FinalizedNoteScreen(ItemStack stack, DataComponentType<FinalizedNoteContent> componentType) {
        super(Component.literal("Finalized Note Screen"));

        FINALIZED_NOTE_COMPONENT_TYPE = componentType;

        FinalizedNoteContent content = stack.get(FINALIZED_NOTE_COMPONENT_TYPE);
        if (content != null) {
            text = content.text().get(false);
            images = content.images();
        } else {
            text = FormattedText.EMPTY;
        }

        for (Image image: images) {
            ImageHelper.downloadImage(image.url());
        }
    }

    @Override
    protected void init() {
        X_OFFSET = (width - 128) / 2 + 4;

        Button doneButton = new Button.Builder(
                Component.translatable("gui.done"), button -> onClose()).build();
        doneButton.setRectangle(170, 20, (width) / 2 - 80 - 5, 190);
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        lines = font.split(text, 118);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), X_OFFSET + 1, 21 + i * 9, 0xFF000000, false);
        }

        for (int i = images.size() - 1; i >= 0; i--) {
            Image image = images.get(i);
            ResourceLocation id = ImageHelper.getImageID(image.url());
            guiGraphics.blit(id, image.x() + X_OFFSET, image.y() + 22,
                    image.width(), image.height(), 0, 0,
                    image.width(), image.height(), image.width(), image.height());
        }

        Style style = getStyleAt(mouseX, mouseY);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(font, style, mouseX, mouseY);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Style style = this.getStyleAt(mouseX, mouseY);
            if (style != null && handleComponentClicked(style)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleComponentClicked(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return false;
        } else {
            boolean bl = super.handleComponentClicked(style);
            if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                onClose();
            }

            return bl;
        }
    }

    @Nullable
    public Style getStyleAt(double x, double y) {
        if (!lines.isEmpty()) {
            int i = (int) x - X_OFFSET;
            int j = (int) y - 22;
            if (i >= 0 && j >= 0) {
                if (i <= 118) {
                    int l = j / 9;
                    if (l < lines.size()) {
                        FormattedCharSequence line = lines.get(l);
                        return font.getSplitter().componentStyleAtWidth(line, i);
                    }
                }
            }
        }
        return null;
    }
}
