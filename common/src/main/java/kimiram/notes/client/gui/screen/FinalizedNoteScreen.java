package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.util.ImageHelper;
import kimiram.notes.item.component.FinalizedNoteContent;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static kimiram.notes.Constants.MOD_ID;

public class FinalizedNoteScreen extends Screen {
    private int X_OFFSET;

    protected DataComponentType<FinalizedNoteContent> FINALIZED_NOTE_COMPONENT_TYPE;
    private final Component text;
    private List<Image> images = new ArrayList<>();

    public FinalizedNoteScreen(ItemStack stack, DataComponentType<FinalizedNoteContent> componentType) {
        super(Component.literal("Finalized Note Screen"));

        FINALIZED_NOTE_COMPONENT_TYPE = componentType;

        FinalizedNoteContent content = stack.get(FINALIZED_NOTE_COMPONENT_TYPE);
        if (content != null) {
            text = content.text().get(false);
            images = content.images();
        } else {
            text = Component.empty();
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
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR));

        for (int i = images.size() - 1; i >= 0; i--) {
            Image image = images.get(i);
            Identifier id = ImageHelper.getImageID(image.url());
            graphics.blit(RenderPipelines.GUI_TEXTURED, id, image.x() + X_OFFSET, image.y() + 22,
                    0, 0, image.width(), image.height(),
                    image.width(), image.height(), image.width(), image.height());
        }
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/note.png"),
                (width - 128) / 2 - 16, 12, 0, 0,
                256, 256, 256, 256, 256, 256);
    }

    private void visitText(ActiveTextCollector activeTextCollector) {
        FormattedText formattedText = ComponentUtils.mergeStyles(text, Style.EMPTY.withoutShadow().withColor(-16777216));
        List<FormattedCharSequence> lines = font.split(formattedText, 120);
        int k = Math.min(160 / 9, lines.size());
        for (int i = 0; i < k; i++) {
            activeTextCollector.accept(X_OFFSET, 20 + i * 9, lines.get(i));
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == 0) {
            ActiveTextCollector.ClickableStyleFinder clickableStyleFinder =
                    new ActiveTextCollector.ClickableStyleFinder(this.font, (int)event.x(), (int)event.y());
            visitText(clickableStyleFinder);
            Style style = clickableStyleFinder.result();
            if (style != null && handleClickEvent(style.getClickEvent())) {
                return true;
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    protected boolean handleClickEvent(@Nullable ClickEvent clickEvent) {
        if (clickEvent == null) {
            return false;
        } else {
            LocalPlayer localPlayer = Objects.requireNonNull(this.minecraft.player, "Player not available");
            if (clickEvent instanceof ClickEvent.RunCommand(String var9)) {
                clickCommandAction(localPlayer, var9, null);
            } else {
                defaultHandleGameClickEvent(clickEvent, this.minecraft, this);
            }

            return true;
        }
    }
}
