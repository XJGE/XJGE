package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

/**
 * An abstract class which can be used to define subclasses that will comprise individual elements of a user interface.
 */
public abstract class Widget {

    protected int width;
    protected int height;
    
    protected static Font defaultFont;
    
    private final Text text  = new Text();
    public Vector3i position = new Vector3i();
    
    /**
     * Creates a new widget object that can be used to organize individual elements of a user interface.
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
    public abstract void update(); //TODO: cursorX/Y?
    
    /**
     * Organizes calls to the OpenGL API made by this widget.
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * Called automatically anytime a change to the applications viewports occurs. Using this method, widgets can update the positions 
     * of their elements to better suit the size of the viewport.
     */
    public abstract void setSplitPosition();
    
    /**
     * Compares the z-position of this widget to another to determine the order in which they're to be rendered. Widgets with a higher 
     * z-index will be drawn further into the background.
     * 
     * @param widget the other widget to compare this one with
     * 
     * @return the difference between this widgets z-position and the z-position of the widget provided
     */
    int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
    /**
     * Draws a string of text to the screen.
     * 
     * @param font     the font the text will be drawn in
     * @param text     the string of text to display
     * @param position the position in the window at which the text string will be rendered
     * @param color    the color the text will be rendered in
     */
    protected void drawString(Font font, String text, Vector2i position, Color color) {
        this.text.drawString(font, text, position, color);
    }
    
    /**
     * Resets the order in which the strings are rendered. This is called automatically by the engine to further optimize text 
     * rendering.
     */
    void resetStringIndex() {
        text.resetStringIndex();
    }
    
}