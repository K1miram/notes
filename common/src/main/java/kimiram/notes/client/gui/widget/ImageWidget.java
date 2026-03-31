package kimiram.notes.client.gui.widget;

import kimiram.notes.client.gui.cursor.StandardCursors;
import kimiram.notes.client.util.ImageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ImageWidget extends AbstractWidget {
    private final String imageUrl;

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

    public ImageWidget(String url, int x, int y, int width, int height, int leftBorder, int rightBorder, int topBorder, int bottomBorder) {
        super(x, y, width, height, Component.empty());

        ImageHelper.downloadImage(url);

        imageUrl = url;

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

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

    }

    public void renderImage(GuiGraphicsExtractor guiGraphics) {
        if (isHoveredOrFocused()) {
            int x = getX(), y = getY();
            guiGraphics.fill(x, y, x + width, y + 1, 0x5F5F5F5F);
            guiGraphics.fill(x + width - 1, y, x + width, y + height, 0x5F5F5F5F);
            guiGraphics.fill(x, y + height - 1, x + width, y + height, 0x5F5F5F5F);
            guiGraphics.fill(x, y, x + 1, y + height, 0x5F5F5F5F);
        }

        Identifier id = ImageHelper.getImageID(imageUrl);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, id, getX(), getY(), 0, 0,
                width, height, width, height, width, height);
    }

    public boolean changeCursor(GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        if (xside != null || yside != null) {
            if ((xside == Sides.LEFT && yside == Sides.TOP) || (xside == Sides.RIGHT && yside == Sides.BOTTOM)) {
                guiGraphics.requestCursor(StandardCursors.RESIZE_NWSE);
            } else if ((xside == Sides.LEFT && yside == Sides.BOTTOM) || (xside == Sides.RIGHT && yside == Sides.TOP)) {
                guiGraphics.requestCursor(StandardCursors.RESIZE_NESW);
            } else if (xside != null) {
                guiGraphics.requestCursor(StandardCursors.RESIZE_EW);
            } else {
                guiGraphics.requestCursor(StandardCursors.RESIZE_NS);
            }
            return true;
        } else {
            if (isMouseOver(mouseX, mouseY)) {
                if ((isLeft(mouseX) && isTop(mouseY)) || (isRight(mouseX) && isBottom(mouseY))) {
                    guiGraphics.requestCursor(StandardCursors.RESIZE_NWSE);
                } else if ((isLeft(mouseX) && isBottom(mouseY)) || (isRight(mouseX) && isTop(mouseY))) {
                    guiGraphics.requestCursor(StandardCursors.RESIZE_NESW);
                } else if (isLeft(mouseX) || isRight(mouseX)) {
                    guiGraphics.requestCursor(StandardCursors.RESIZE_EW);
                } else if (isTop(mouseY) || isBottom(mouseY)) {
                    guiGraphics.requestCursor(StandardCursors.RESIZE_NS);
                } else {
                    guiGraphics.requestCursor(StandardCursors.ARROW);
                }
                return true;
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
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            if (event.button() == 0) {
                if (isLeft(event.x())) {
                    xside = Sides.LEFT;
                } else if (isRight(event.x())) {
                    xside = Sides.RIGHT;
                }

                if (isTop(event.y())) {
                    yside = Sides.TOP;
                } else if (isBottom(event.y())) {
                    yside = Sides.BOTTOM;
                }

                return true;
            }
            if (event.button() == 1) {
                active = false;
                visible = false;

                playDownSound(Minecraft.getInstance().getSoundManager());

                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDrag(@NotNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (xside == Sides.LEFT) {
            if (dwidth - mouseX >= 5 && dx + mouseX >= leftBorder) {
                dwidth -= mouseX;
                dx = getX() + width - (int) dwidth;
            }
        } else if (xside == Sides.RIGHT) {
            if (dwidth + mouseX >= 5 && dx + dwidth + mouseX <= rightBorder) {
                dwidth += mouseX;
            }
        }

        if (yside == Sides.TOP) {
            if (dheight - mouseY >= 5 && dy + mouseY >= topBorder) {
                dheight -= mouseY;
                dy = getY() + height - (int) dheight;
            }
        } else if (yside == Sides.BOTTOM) {
            if (dheight + mouseY >= 5 && dy + dheight + mouseY <= bottomBorder) {
                dheight += mouseY;
            }
        }

        if (xside == null && yside == null) {
            if (dx + mouseX >= leftBorder && dx + mouseX + dwidth <= rightBorder) {
                dx += mouseX;
            }
            if (dy + mouseY >= topBorder && dy + mouseY + dheight <= bottomBorder) {
                dy += mouseY;
            }
        }

        setRectangle((int) dwidth, (int) dheight, (int) dx, (int) dy);
    }

    @Override
    public void onRelease(@NotNull MouseButtonEvent event) {
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
