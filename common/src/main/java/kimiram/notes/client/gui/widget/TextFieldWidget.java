package kimiram.notes.client.gui.widget;

import kimiram.notes.client.gui.cursor.StandardCursors;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class TextFieldWidget extends AbstractWidget {
    private Font font;
    private String text;
    private final List<Line> lines = new ArrayList<>();
    private final TextFieldHelper textFieldHelper = new TextFieldHelper(
            () -> text != null ? text : "",
            (String newText) -> text = newText,
            () -> TextFieldHelper.getClipboardContents(Minecraft.getInstance()),
            (String clipboardValue) -> TextFieldHelper.setClipboardContents(Minecraft.getInstance(), clipboardValue),
            string -> font.wordWrapHeight(string, width - 2) <= (height - 2)
    );
    private Position cursorPos = new Position(getX() + 1, getY() + 1);
    private int currentLine;
    private long lastClickTime = 0;
    private int lastClickPos = -1;
    private final List<Rect2i> selectionBoxes = new ArrayList<>();

    public TextFieldWidget(Font font, int x, int y, int width, int height, String text) {
        super(x, y, width, height, Component.empty());

        this.font = font;
        this.text = text != null ? text : "";

        updateLines();
    }

    public String getText() {
        return text;
    }

    private void updateLines() {
        lines.clear();
        selectionBoxes.clear();
        int cp = textFieldHelper.getCursorPos();
        int sp = textFieldHelper.getSelectionPos();
        int s = Math.min(cp, sp);
        int e = Math.max(cp, sp);
        StringSplitter splitter = font.getSplitter();
        splitter.splitLines(text, width - 2, Style.EMPTY, true, (style, start, end) -> {
            if (cp >= start && cp <= end) {
                if ((cp == text.length() && text.endsWith("\n")) ||
                        (text.endsWith(" ") && font.width(text.substring(start, end)) >= width - 2)) {
                    cursorPos = new Position(getX() + 1, getY() + 1 + (lines.size() + 1) * 9);
                    currentLine = lines.size() + 1;
                } else {
                    cursorPos = new Position(getX() + 1 + font.width(text.substring(start, cp)),
                            getY() + 1 + lines.size() * 9);
                    currentLine = lines.size();
                }
            }
            if (textFieldHelper.isSelecting() && s <= end && e >= start) {
                selectionBoxes.add(new Rect2i(
                        getX() + 1 + font.width(text.substring(start, Math.max(start, s))),
                        getY() + 1 + lines.size() * 9,
                        font.width(text.substring(Math.max(start, s), Math.min(end, e))),
                        9));
            }
            lines.add(new Line(StringUtils.stripEnd(text.substring(start, end), " \n"), start, end));
        });
        if (text.endsWith("\n")) {
            lines.add(new Line("", text.length(), text.length()));
        }
        if (lines.isEmpty()) {
            lines.add(new Line("", text.length(), text.length()));
            cursorPos = new Position(getX() + 1, getY() + 1);
            currentLine = 0;
        }
    }

    private int getPosAt(int x, int y) {
        int l = 0;
        while (l < lines.size() - 1 && l * 9 < y) {
            l++;
        }
        int start = lines.get(l).start;
        int end = lines.get(l).end;
        int pos = start;
        while (pos < end - 1 && font.width(text.substring(start, pos)) < x) {
            pos++;
        }
        if (pos == text.length() - 1 && !text.endsWith("\n")) {
            pos++;
        }
        if (pos > start && x - font.width(text.substring(start, pos - 1)) < font.width(text.substring(start, pos)) - x) {
            pos--;
        }
        return pos;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (super.charTyped(codePoint, modifiers)) {
            return true;
        } else if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            textFieldHelper.insertText(Character.toString(codePoint));
            updateLines();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyPressedEditMode(keyCode)) {
            updateLines();
            return true;
        }
        return false;
    }

    public boolean keyPressedEditMode(int keyCode) {
        if (Screen.isSelectAll(keyCode)) {
            textFieldHelper.selectAll();
            return true;
        } else if (Screen.isCopy(keyCode)) {
            textFieldHelper.copy();
            return true;
        } else if (Screen.isPaste(keyCode)) {
            textFieldHelper.paste();
            return true;
        } else if (Screen.isCut(keyCode)) {
            textFieldHelper.cut();
            return true;
        } else {
            TextFieldHelper.CursorStep cursorStep = Screen.hasControlDown() ?
                    TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER:
                case GLFW.GLFW_KEY_KP_ENTER:
                    textFieldHelper.insertText("\n");
                    return true;
                case GLFW.GLFW_KEY_BACKSPACE:
                    textFieldHelper.removeFromCursor(-1, cursorStep);
                    return true;
                case GLFW.GLFW_KEY_DELETE:
                    textFieldHelper.removeFromCursor(1, cursorStep);
                    return true;
                case GLFW.GLFW_KEY_RIGHT:
                    textFieldHelper.moveBy(1, Screen.hasShiftDown(), cursorStep);
                    return true;
                case GLFW.GLFW_KEY_LEFT:
                    textFieldHelper.moveBy(-1, Screen.hasShiftDown(), cursorStep);
                    return true;
                case GLFW.GLFW_KEY_DOWN:
                    if (currentLine == lines.size() - 1) {
                        textFieldHelper.setCursorToEnd(Screen.hasShiftDown());
                    } else {
                        int w = font.width(text.substring(lines.get(currentLine).start, textFieldHelper.getCursorPos()));
                        int pos = getPosAt(w, (currentLine + 1) * 9);
                        textFieldHelper.setCursorPos(pos, Screen.hasShiftDown());
                    }
                    return true;
                case GLFW.GLFW_KEY_UP:
                    if (currentLine == 0) {
                        textFieldHelper.setCursorToStart(Screen.hasShiftDown());
                    } else {
                        int w = font.width(text.substring(lines.get(currentLine).start, textFieldHelper.getCursorPos()));
                        int pos = getPosAt(w, (currentLine - 1) * 9);
                        textFieldHelper.setCursorPos(pos, Screen.hasShiftDown());
                    }
                    return true;
                case GLFW.GLFW_KEY_HOME:
                    if (currentLine != lines.size()) {
                        if (Screen.hasControlDown()) {
                            textFieldHelper.setCursorToStart(Screen.hasShiftDown());
                        } else {
                            textFieldHelper.setCursorPos(lines.get(currentLine).start, Screen.hasShiftDown());
                        }
                    }
                    return true;
                case GLFW.GLFW_KEY_END:
                    if (currentLine != lines.size()) {
                        if (Screen.hasControlDown()) {
                            textFieldHelper.setCursorToEnd(Screen.hasShiftDown());
                        } else {
                            textFieldHelper.setCursorPos(lines.get(currentLine).end, Screen.hasShiftDown());
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!text.isEmpty()) {
            long l = Util.getMillis();
            int pos = getPosAt((int) mouseX - getX() - 1, (int) mouseY - getY() - 9);
            if (pos != lastClickPos || l - lastClickTime >= 250) {
                textFieldHelper.setCursorPos(pos, Screen.hasShiftDown());
            } else if (textFieldHelper.isSelecting()) {
                textFieldHelper.selectAll();
            } else {
                textFieldHelper.setSelectionRange(
                        StringSplitter.getWordPosition(text, -1, pos, false),
                        StringSplitter.getWordPosition(text, 1, pos, false)
                );
            }

            updateLines();

            lastClickPos = pos;
            lastClickTime = l;
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (!text.isEmpty()) {
            int pos = getPosAt((int) mouseX - getX() - 1, (int) mouseY - getY() - 9);
            textFieldHelper.setCursorPos(pos, true);
            updateLines();
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (visible && active) {
            for (int i = 0; i < lines.size(); i++) {
                guiGraphics.drawString(font, lines.get(i).text, getX() + 1, getY() + 1 + i * 9,
                        0xFF000000, false);
            }

            if (isFocused()) {
                for (Rect2i rect2i: selectionBoxes) {
                    int i = rect2i.getX();
                    int j = rect2i.getY();
                    int k = i + rect2i.getWidth();
                    int l = j + rect2i.getHeight();
                    guiGraphics.fill(RenderType.guiTextHighlight(), i, j, k, l, -16776961);
                }

                drawCursor(guiGraphics, textFieldHelper.getCursorPos() == text.length());
            }
        }
    }

    public void changeMouseCursor() {
        if (visible && active) {
            if (isHovered) {
                StandardCursors.IBEAM.applyTo(Minecraft.getInstance().getWindow());
            } else {
                StandardCursors.ARROW.applyTo(Minecraft.getInstance().getWindow());
            }
        }
    }

    private void drawCursor(GuiGraphics guiGraphics, boolean atTheEnd) {
        long ticks = Util.getMillis() / 50;
        if (ticks / 6 % 2 == 0) {
            if (atTheEnd) {
                guiGraphics.drawString(font, "_", cursorPos.x, cursorPos.y, 0xFF000000, false);
            } else {
                guiGraphics.fill(cursorPos.x - 1, cursorPos.y - 1, cursorPos.x, cursorPos.y + 9,
                        0xFF000000);
            }
        }
    }

    private record Line(String text, int start, int end) {
    }

    private record Position(int x, int y) {
    }


    @Override
    public void playDownSound(@NotNull SoundManager handler) {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
