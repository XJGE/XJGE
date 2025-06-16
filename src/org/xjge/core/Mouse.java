package org.xjge.core;

/**
 * Created: Apr 9, 2024
 * 
 * @author J Hoffman
 * @since  2.4.6
 */
public final class Mouse {
    
    boolean currentClickValue;
    boolean previousClickValue;
    boolean leftHeld;
    boolean middleHeld;
    boolean rightHeld;
    
    double cursorPositionX;
    double cursorPositionY;
    double scrollSpeedX;
    double scrollSpeedY;
    
    int button;
    int mods;
    
    /**
     * Used to determine whether or not a button on the mouse is currently 
     * being pressed.
     * 
     * @return if true, a button on the mouse is being held
     */
    public boolean clicked() {
        return currentClickValue;
    }
    
    /**
     * Used to determine if a button on the mouse has been pressed. This method
     * will only return true upon the initial press.
     * 
     * @return if true, a button on the mouse has been pressed
     */
    public boolean clickedOnce() {
        return currentClickValue == true && previousClickValue != currentClickValue;
    }
    
    /**
     * Obtains the current position of the mouse cursor within the windows 
     * content area.
     * <br><br>
     * NOTE: if a call to {@linkplain Window#setResolution(int, int)} has been 
     * made, then the value returned by this method will be converted to reflect
     * the cursors position relative to the pixel scale of the windows resolution.
     * 
     * @return the horizontal position of the mouse cursor in screen coordinates
     */
    public double getCursorPositionX() {
        return cursorPositionX;
    }
    
    /**
     * Obtains the current position of the mouse cursor within the windows 
     * content area.
     * <br><br>
     * NOTE: if a call to {@linkplain Window#setResolution(int, int)} has been 
     * made, then the value returned by this method will be converted to reflect
     * the cursors position relative to the pixel scale of the windows resolution.
     * 
     * @return the vertical position of the mouse cursor in screen coordinates
     */
    public double getCursorPositionY() {
        return cursorPositionY;
    }
    
    /**
     * Obtains the current input value of the horizontal scroll wheel.
     * 
     * @return a positive or negative number indicating which direction the 
     *         scroll wheel is being moved in
     */
    public double getScrollSpeedX() {
        return scrollSpeedX;
    }
    
    /**
     * Obtains the current input value of the vertical scroll wheel.
     * 
     * @return a positive or negative number indicating which direction the 
     *         scroll wheel is being moved in
     */
    public double getScrollSpeedY() {
        return scrollSpeedY;
    }
    
    /**
     * Obtains the ID number of the last mouse button which was used.
     * 
     * @return a number provided by GLFW used to identify which button is in use
     */
    public int getButtonID() {
        return button;
    }
    
    /**
     * Obtains the ID number of whichever modifier key (shift, alt, ctrl, etc.) 
     * is currently in use.
     * 
     * @return a number provided by GLFW used to identify which key is being used
     */
    public int getModifierID() {
        return mods;
    }
    
}