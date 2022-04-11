package org.xjge.core;

import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;

//Created: May 12, 2021

/**
 * An abstract class which can be used to define subclasses that will 
 * comprise individual elements of a user interface.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Widget {

    protected int width;
    protected int height;
    
    protected static Font defaultFont;
    
    private final Text text  = new Text();
    public Vector3i position = new Vector3i();
    
    /**
     * Creates a new widget object that can be used to organize individual 
     * elements of a user interface.
     * 
     * @param position the position of the widget on the screen
     * @param width    the width of the widget
     * @param height   the height of the widget
     */
    public Widget(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    /**
     * Updates the internal logic of the widget.
     */
    public abstract void update();
    
    /**
     * Organizes calls to the OpenGL API made by this widget.
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * Called automatically anytime a change to the applications viewports 
     * occurs.Using this method, widgets can update the positions of their 
     * elements to better suit the size of the viewport.
     * <p>
     * The arguments found here are provided out of convenience and can be 
     * accessed statically though the {@link XJGE} class if the implementation 
     * prefers.
     * 
     * @param split          the current split value used to divide the screen
     * @param viewportWidth  the width (in internal resolution pixels) of the 
     *                       viewport rendering this widget
     * @param viewportHeight the height (in internal resolution pixels) of the
     *                       viewport rendering this widget
     */
    public abstract void setSplitPosition(Split split, int viewportWidth, int viewportHeight);
    
    /**
     * Processes input from the keyboard captured by the game window.
     * <p>
     * NOTE: Only the first viewport (ID: 0) will receive input from the 
     * keyboard.
     * 
     * @param key    the value supplied by GLFW of a single key on the keyboard
     * @param action an action supplied by GLFW that describes the nature of 
     *               the key press
     * @param mods   a value supplied by GLFW denoting whether any mod keys 
     *               where held (such as shift or control)
     * 
     * @see Input#getKeyChar(int, int)
     */
    public abstract void processKeyInput(int key, int action, int mods);
    
    /**
     * Used to free resources used by the widget when its removed from a 
     * viewport.
     */
    public abstract void destroy();
    
    /**
     * Compares the z-position of this widget to another to determine the order 
     * in which they're to be rendered. Widgets with a higher z-index will be 
     * drawn further into the background.
     * 
     * @param widget the other widget to compare this one with
     * 
     * @return the difference between this widgets z-position and the 
     *         z-position of the widget provided
     */
    int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
    /**
     * Draws a string of text to the screen.
     * 
     * @param font     the font the text will be drawn in
     * @param text     the string of text to display
     * @param position the position in the window at which the text string will 
     *                 be rendered
     * @param color    the color the text will be rendered in
     */
    protected void drawString(Font font, String text, Vector2i position, Color color) {
        this.text.drawString(font, text, position, color);
    }
    
    /**
     * Resets the order in which the strings are rendered. This is called 
     * automatically by the engine to further optimize text rendering.
     */
    void resetStringIndex() {
        text.resetStringIndex();
    }
    
}