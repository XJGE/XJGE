package org.xjge.ui;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.graphics.GLProgram;

/**
 * Organizes smaller elements (such as buttons, text, menus, etc.) that comprise
 * part of a larger user interface.
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public abstract class Widget {
    
    /**
     * Organizes the internal logic of this widget. Called automatically by the 
     * engine once every game tick. 
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    public abstract void update(double targetDelta, double trueDelta);
    
    /**
     * Organizes the rendering logic of elements contained by this widget
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * Called automatically anytime a change to the applications viewports 
     * occurs. Using this method, widgets can update the positions of their 
     * elements to better suit the size of the viewport.
     * <br><br>
     * The arguments provided here are done so out of convenience but can be 
     * accessed statically though the {@link Window} class if needed.
     * 
     * @param splitType the value that denotes how the screen is being divided
     * @param viewportWidth the width (in internal resolution pixels) of the 
     *                      viewport rendering this widget
     * @param viewportHeight the height (in internal resolution pixels) of the
     *                       viewport rendering this widget
     */
    public abstract void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight);
    
    /**
     * Called automatically anytime input is performed using the keyboard.
     * 
     * @param key a number provided by GLFW used to identify which key was pressed
     * @param action used to denote the type of action performed on the key 
     *               (GLFW_PRESS, GLFW_RELEASE, etc.)
     * @param mods a number provided by GLFW that indicates whether or not any 
     *             modifier key (shift, alt, etc.) was in use concurrently
     */
    public abstract void processKeyboardInput(int key, int action, int mods);
    
    /**
     * Called automatically anytime input is performed using the mouse.
     * 
     * @param mouse an object containing the input state of the users mouse
     */
    public abstract void processMouseInput(Mouse mouse);
    
    /**
     * Called once automatically after a request has been made to remove this 
     * widget. Any elements which may have allocated memory should free it 
     * inside this method.
     */
    public abstract void delete();
    
}