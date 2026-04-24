package kimiram.notes.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static kimiram.notes.Constants.MOD_ID;

public class TrashCanButton extends Button {
    private static final ResourceLocation TRASH_CAN_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/trash_can");
    private static final ResourceLocation TRASH_CAN_HIGHLIGHTED_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "widget/trash_can_highlighted");

    public TrashCanButton(int x, int y, OnPress onPress) {
        super(x, y, 16, 16, Component.literal("Delete"), onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                isHoveredOrFocused() ? TRASH_CAN_HIGHLIGHTED_SPRITE : TRASH_CAN_SPRITE,
                getX(), getY(), width, height
        );
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return false;
    }
}
