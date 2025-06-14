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
    
    double cursorPositionX;
    double cursorPositionY;
    
    int button;
    
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
     * Obtains the ID number of the last mouse button which was used.
     * 
     * @return a number provided by GLFW used to identify which button is in use
     */
    public int getButtonID() {
        return button;
    }
    
}