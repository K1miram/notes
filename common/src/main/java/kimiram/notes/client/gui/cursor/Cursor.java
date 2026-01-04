package kimiram.notes.client.gui.cursor;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

public class Cursor {
    public static final Cursor DEFAULT = new Cursor("default", 0L);
    private final String name;
    private final long handle;

    private Cursor(String name, long handle) {
        this.name = name;
        this.handle = handle;
    }

    public void applyTo(Window window) {
        GLFW.glfwSetCursor(window.getWindow(), this.handle);
    }

    public String toString() {
        return this.name;
    }

    public static Cursor createStandard(int handle, String name, Cursor fallback) {
        long l = GLFW.glfwCreateStandardCursor(handle);
        return l == 0L ? fallback : new Cursor(name, l);
    }
}