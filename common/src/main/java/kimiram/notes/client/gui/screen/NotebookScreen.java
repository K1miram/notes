package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.gui.widget.TrashCanButton;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NotebookContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.Constants.imageHelper;
import static kimiram.notes.item.component.ModDataComponents.NOTEBOOK_COMPONENT_TYPE;

public class NotebookScreen extends Screen {
    private static final Identifier NOTE_BACKGROUND_ID = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/note.png");
    private final Identifier backgroundTextureId;
    private final boolean isFinalized;

    private final OnPageRemove onPageRemove;
    private final OnNotebookFinalize onNotebookFinalize;

    private List<FinalizedNoteContent> pages = new ArrayList<>();
    private int currentPage = 0;

    private int leftPageOffset;
    private Component leftText;
    private List<Image> leftImages;
    private int rightPageOffset;
    private Component rightText;
    private List<Image> rightImages;

    private TrashCanButton removeLeftPageButton;
    private TrashCanButton removeRightPageButton;

    private PageButton backButton;
    private PageButton forwardButton;

    public NotebookScreen(ItemStack stack, Identifier id, OnPageRemove onPageRemove, OnNotebookFinalize onNotebookFinalize) {
        super(Component.literal("Notebook Screen"));

        NotebookContent content = stack.get(NOTEBOOK_COMPONENT_TYPE);
        if (content != null) {
            this.pages = content.pages();
        }

        this.backgroundTextureId = id;
        this.isFinalized = false;

        this.onPageRemove = onPageRemove;
        this.onNotebookFinalize = onNotebookFinalize;

        updatePages();
    }

    public NotebookScreen(ItemStack stack, Identifier id) {
        super(Component.literal("Notebook Screen"));

        NotebookContent content = stack.get(NOTEBOOK_COMPONENT_TYPE);
        if (content != null) {
            this.pages = content.pages();
        }

        this.backgroundTextureId = id;
        this.isFinalized = true;

        this.onPageRemove = null;
        this.onNotebookFinalize = null;

        updatePages();
    }

    private void updatePages() {
        leftText = Component.empty();
        leftImages = new ArrayList<>();
        if (currentPage < pages.size()) {
            FinalizedNoteContent page = pages.get(currentPage);
            if (page != null) {
                leftText = page.text().get(false);
                leftImages = page.images();
            }
        }

        rightText = Component.empty();
        rightImages = new ArrayList<>();
        if (currentPage + 1 < pages.size()) {
            FinalizedNoteContent page = pages.get(currentPage + 1);
            if (page != null) {
                rightText = page.text().get(false);
                rightImages = page.images();
            }
        }
    }

    private void updateButtonsVisibility() {
        if (!isFinalized) {
            removeLeftPageButton.visible = currentPage < pages.size();
            removeRightPageButton.visible = currentPage + 1 < pages.size();
        }

        backButton.visible = currentPage - 2 >= 0;
        forwardButton.visible = currentPage + 2 < pages.size();
    }

    @Override
    protected void init() {
        super.init();

        leftPageOffset = width / 2 - 128 - 10 + 4;
        rightPageOffset = width / 2 + 10 + 4;

        if (!isFinalized) {
            removeLeftPageButton = addRenderableWidget(new TrashCanButton(
                    width / 2 - 10 - 128 - 8, 7, button -> removeLeftPage()
            ));

            removeRightPageButton = addRenderableWidget(new TrashCanButton(
                    width / 2 + 10 + 128 - 8, 7, button -> removeRightPage()
            ));
        }

        backButton = addRenderableWidget(new PageButton(
                width / 2 - 10 - 128 - 5, 12 + 162, false, button -> moveBack(), true)
        );

        forwardButton = addRenderableWidget(new PageButton(
                width / 2 + 10 + 128 - 17, 12 + 162, true, button -> moveForward(), true)
        );

        if (!isFinalized) {
            addRenderableWidget(Button.builder(
                            Component.translatable("gui.notes.finalize"),
                            button -> finalizeNotebook())
                    .bounds(width / 2 - 10 - 128, 12 + 8 + 168 + 10, 128, 20).build()
            );

            addRenderableWidget(Button.builder(
                            Component.translatable("gui.done"),
                            button -> onClose())
                    .bounds(width / 2 + 10, 12 + 8 + 168 + 10, 128, 20).build()
            );
        } else {
            addRenderableWidget(Button.builder(
                            Component.translatable("gui.done"),
                            button -> onClose())
                    .bounds((width - 192) / 2, 12 + 8 + 168 + 10, 192, 20).build()
            );
        }

        updateButtonsVisibility();
    }

    private void removeLeftPage() {
        onPageRemove.sendPacket(currentPage);
        Minecraft.getInstance().setScreen(null);
    }

    private void removeRightPage() {
        onPageRemove.sendPacket(currentPage + 1);
        Minecraft.getInstance().setScreen(null);
    }

    private void moveBack() {
        if (currentPage - 2 >= 0) {
            currentPage -= 2;
        }
        updatePages();
        updateButtonsVisibility();
    }

    private void moveForward() {
        if (currentPage + 2 < pages.size()) {
            currentPage += 2;
        }
        updatePages();
        updateButtonsVisibility();
    }

    private void finalizeNotebook() {
        onNotebookFinalize.sendPacket();
        onClose();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR), leftText, leftPageOffset);

        for (int i = leftImages.size() - 1; i >= 0; i--) {
            Image image = leftImages.get(i);
            Identifier id = imageHelper.getImageId(image.url(), image.type());
            graphics.blit(RenderPipelines.GUI_TEXTURED, id, image.x() + leftPageOffset, image.y() + 22,
                    0, 0, image.width(), image.height(),
                    image.width(), image.height(), image.width(), image.height());
        }

        visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR), rightText, rightPageOffset);

        for (int i = rightImages.size() - 1; i >= 0; i--) {
            Image image = rightImages.get(i);
            Identifier id = imageHelper.getImageId(image.url(), image.type());
            graphics.blit(RenderPipelines.GUI_TEXTURED, id, image.x() + rightPageOffset, image.y() + 22,
                    0, 0, image.width(), image.height(),
                    image.width(), image.height(), image.width(), image.height());
        }
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTextureId, (width - 296) / 2, 6, 0, 0,
        296, 186, 296, 186, 296, 186);

        if (currentPage < pages.size()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, NOTE_BACKGROUND_ID, width / 2 - 16 - 128 - 10, 12, 0, 0,
            256, 256, 256, 256, 256, 256);
        }
        if (currentPage + 1 < pages.size()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, NOTE_BACKGROUND_ID, width / 2 - 16 + 10, 12, 0, 0,
                    256, 256, 256, 256, 256, 256);
        }
    }

    private void visitText(ActiveTextCollector activeTextCollector, Component text, int offset) {
        FormattedText formattedText = ComponentUtils.mergeStyles(text, Style.EMPTY.withoutShadow().withColor(-16777216));
        List<FormattedCharSequence> lines = font.split(formattedText, 120);
        int k = Math.min(160 / 9, lines.size());
        for (int i = 0; i < k; i++) {
            activeTextCollector.accept(offset, 20 + i * 9, lines.get(i));
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == 0) {
            ActiveTextCollector.ClickableStyleFinder clickableStyleFinder =
                    new ActiveTextCollector.ClickableStyleFinder(this.font, (int)event.x(), (int)event.y());
            visitText(clickableStyleFinder, leftText, leftPageOffset);
            visitText(clickableStyleFinder, rightText, rightPageOffset);
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

    public interface OnPageRemove {
        void sendPacket(int pageIndex);
    }

    public interface OnNotebookFinalize {
        void sendPacket();
    }
}
