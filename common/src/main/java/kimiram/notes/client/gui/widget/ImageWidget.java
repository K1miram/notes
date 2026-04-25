package kimiram.notes.client.gui.widget;

import com.mojang.blaze3d.platform.Window;
import kimiram.imagelib.ImageLib;
import kimiram.notes.client.gui.cursor.StandardCursors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.client.ClientConstants.imageHelper;

public class ImageWidget extends AbstractWidget {
    private final String imageUrl;
    private final ImageLib.Type imageType;

    private double dx;
    private double dy;
    private double dwidth;
    private double dheight;

    private Sides xside = null;
    private Sides yside = null;

    private final int leftBorder;
    private final int rightBorder;
    private final int topBorder;
    private final int bottomBorder;

    public ImageWidget(String url, ImageLib.Type type, int x, int y, int width, int height, int leftBorder, int rightBorder, int topBorder, int bottomBorder) {
        super(x, y, width, height, Component.empty());

        imageHelper.downloadImage(url, type);

        imageUrl = url;
        imageType = type;

        dx = x;
        dy = y;
        dwidth = width;
        dheight = height;

        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.topBorder = topBorder;
        this.bottomBorder = bottomBorder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ImageLib.Type getImageType() {
        return imageType;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    public void renderImage(GuiGraphics guiGraphics) {
        if (isHoveredOrFocused()) {
            guiGraphics.renderOutline(getX(), getY(), width, height, 0x5F5F5F5F);
        }

        ResourceLocation id = imageHelper.getImageId(imageUrl, imageType);
        guiGraphics.blit(id, getX(), getY(), width, height, 0, 0, width, height, width, height);
    }

    public boolean changeCursor(double mouseX, double mouseY) {
        Window window = Minecraft.getInstance().getWindow();
        if (xside != null || yside != null) {
            if ((xside == Sides.LEFT && yside == Sides.TOP) || (xside == Sides.RIGHT && yside == Sides.BOTTOM)) {
                StandardCursors.RESIZE_NWSE.applyTo(window);
            } else if ((xside == Sides.LEFT && yside == Sides.BOTTOM) || (xside == Sides.RIGHT && yside == Sides.TOP)) {
                StandardCursors.RESIZE_NESW.applyTo(window);
            } else if (xside != null) {
                StandardCursors.RESIZE_EW.applyTo(window);
            } else {
                StandardCursors.RESIZE_NS.applyTo(window);
            }
            return true;
        } else {
            if (isMouseOver(mouseX, mouseY)) {
                if ((isLeft(mouseX) && isTop(mouseY)) || (isRight(mouseX) && isBottom(mouseY))) {
                    StandardCursors.RESIZE_NWSE.applyTo(window);
                } else if ((isLeft(mouseX) && isBottom(mouseY)) || (isRight(mouseX) && isTop(mouseY))) {
                    StandardCursors.RESIZE_NESW.applyTo(window);
                } else if (isLeft(mouseX) || isRight(mouseX)) {
                    StandardCursors.RESIZE_EW.applyTo(window);
                } else if (isTop(mouseY) || isBottom(mouseY)) {
                    StandardCursors.RESIZE_NS.applyTo(window);
                } else {
                    StandardCursors.ARROW.applyTo(window);
                }
                return true;
            } else {
                StandardCursors.ARROW.applyTo(window);
            }
        }
        return false;
    }

    private boolean isLeft(double x) {
        return getX() <= x && x <= getX() + 3;
    }

    private boolean isRight(double x) {
        return getRight() - 3 <= x && x <= getRight();
    }

    private boolean isTop(double y) {
        return getY() <= y && y <= getY() + 3;
    }

    private boolean isBottom(double y) {
        return getBottom() - 3 <= y && y <= getBottom();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && visible) {
            if (isMouseOver(mouseX, mouseY)) {
                if (button == 0) {
                    if (isLeft(mouseX)) {
                        xside = Sides.LEFT;
                    } else if (isRight(mouseX)) {
                        xside = Sides.RIGHT;
                    }

                    if (isTop(mouseY)) {
                        yside = Sides.TOP;
                    } else if (isBottom(mouseY)) {
                        yside = Sides.BOTTOM;
                    }

                    return true;
                }
                if (button == 1) {
                    active = false;
                    visible = false;

                    playDownSound(Minecraft.getInstance().getSoundManager());

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (xside == Sides.LEFT) {
            if (dwidth - dragX >= 5 && dx + dragX >= leftBorder) {
                dwidth -= dragX;
                dx = getX() + width - (int) dwidth;
            }
        } else if (xside == Sides.RIGHT) {
            if (dwidth + dragX >= 5 && dx + dwidth + dragX <= rightBorder) {
                dwidth += dragX;
            }
        }

        if (yside == Sides.TOP) {
            if (dheight - dragY >= 5 && dy + dragY >= topBorder) {
                dheight -= dragY;
                dy = getY() + height - (int) dheight;
            }
        } else if (yside == Sides.BOTTOM) {
            if (dheight + dragY >= 5 && dy + dheight + dragY <= bottomBorder) {
                dheight += dragY;
            }
        }

        if (xside == null && yside == null) {
            if (dx + dragX >= leftBorder && dx + dragX + dwidth <= rightBorder) {
                dx += dragX;
            }
            if (dy + dragY >= topBorder && dy + dragY + dheight <= bottomBorder) {
                dy += dragY;
            }
        }

        setRectangle((int) dwidth, (int) dheight, (int) dx, (int) dy);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        xside = null;
        yside = null;

        dx = (int) dx;
        dy = (int) dy;
        dwidth = (int) dwidth;
        dheight = (int) dheight;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    private enum Sides {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }
}
