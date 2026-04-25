package kimiram.notes.client.gui.screen;

import kimiram.notes.Image;
import kimiram.notes.client.gui.widget.TrashCanButton;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NotebookContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.client.ClientConstants.imageHelper;
import static kimiram.notes.item.component.ModDataComponents.NOTEBOOK_COMPONENT_TYPE;

public class NotebookScreen extends Screen {
    private static final ResourceLocation NOTE_BACKGROUND_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/note.png");
    private final ResourceLocation backgroundTextureId;
    private final boolean isFinalized;

    private final OnPageRemove onPageRemove;
    private final OnNotebookFinalize onNotebookFinalize;

    private List<FinalizedNoteContent> pages = new ArrayList<>();
    private int currentPage = 0;

    private int leftPageOffset;
    private Component leftText;
    private List<FormattedCharSequence> leftLines = new ArrayList<>();
    private List<Image> leftImages;
    private int rightPageOffset;
    private Component rightText;
    private List<FormattedCharSequence> rightLines = new ArrayList<>();
    private List<Image> rightImages;

    private TrashCanButton removeLeftPageButton;
    private TrashCanButton removeRightPageButton;

    private PageButton backButton;
    private PageButton forwardButton;

    public NotebookScreen(ItemStack stack, ResourceLocation id, OnPageRemove onPageRemove, OnNotebookFinalize onNotebookFinalize) {
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

    public NotebookScreen(ItemStack stack, ResourceLocation id) {
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        super.render(guiGraphics, mouseX, mouseY, a);

        leftLines = font.split(leftText, 118);
        for (int i = 0; i < leftLines.size(); i++) {
            guiGraphics.drawString(font, leftLines.get(i), leftPageOffset + 1, 21 + i * 9, 0xFF000000, false);
        }

        for (int i = leftImages.size() - 1; i >= 0; i--) {
            Image image = leftImages.get(i);
            ResourceLocation id = imageHelper.getImageId(image.url(), image.type());
            guiGraphics.blit(id, image.x() + leftPageOffset, image.y() + 22,
                    image.width(), image.height(), 0, 0,
                    image.width(), image.height(), image.width(), image.height());
        }

        rightLines = font.split(rightText, 118);
        for (int i = 0; i < rightLines.size(); i++) {
            guiGraphics.drawString(font, rightLines.get(i), rightPageOffset + 1, 21 + i * 9, 0xFF000000, false);
        }

        for (int i = rightImages.size() - 1; i >= 0; i--) {
            Image image = rightImages.get(i);
            ResourceLocation id = imageHelper.getImageId(image.url(), image.type());
            guiGraphics.blit(id, image.x() + rightPageOffset, image.y() + 22,
                    image.width(), image.height(), 0, 0,
                    image.width(), image.height(), image.width(), image.height());
        }


        Style style = getStyleAt(mouseX, mouseY);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(font, style, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        super.renderBackground(guiGraphics, mouseX, mouseY, a);

        guiGraphics.blit(backgroundTextureId, (width - 296) / 2, 6, 296, 186,
                0, 0, 296, 186, 296, 186);

        if (currentPage < pages.size()) {
            guiGraphics.blit(NOTE_BACKGROUND_ID, width / 2 - 16 - 128 - 10, 12, 256, 256,
                    0, 0, 256, 256, 256, 256);
        }
        if (currentPage + 1 < pages.size()) {
            guiGraphics.blit(NOTE_BACKGROUND_ID, width / 2 - 16 + 10, 12, 256, 256,
                    0, 0, 256, 256, 256, 256);
        }
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
        if (!leftLines.isEmpty()) {
            int i = (int) x - leftPageOffset;
            int j = (int) y - 22;
            if (i >= 0 && j >= 0) {
                if (i <= 118) {
                    int l = j / 9;
                    if (l < leftLines.size()) {
                        FormattedCharSequence line = leftLines.get(l);
                        return font.getSplitter().componentStyleAtWidth(line, i);
                    }
                }
            }
        }
        if (!rightLines.isEmpty()) {
            int i = (int) x - rightPageOffset;
            int j = (int) y - 22;
            if (i >= 0 && j >= 0) {
                if (i <= 118) {
                    int l = j / 9;
                    if (l < rightLines.size()) {
                        FormattedCharSequence line = rightLines.get(l);
                        return font.getSplitter().componentStyleAtWidth(line, i);
                    }
                }
            }
        }
        return null;
    }

    public interface OnPageRemove {
        void sendPacket(int pageIndex);
    }

    public interface OnNotebookFinalize {
        void sendPacket();
    }
}
