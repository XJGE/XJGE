package org.xjge.core;

import org.xjge.ui.Rectangle;

/**
 * Represents the peripheral device of the same name.
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
     * Determines whether or not the mouse cursor is currently hovering over the
     * provided UI element.
     * 
     * @param positionX the horizontal position of the boundary areas lower-left corner
     * @param positionY the vertical position of the boundary areas lower-left corner
     * @param width the width of the boundary area (in pixels)
     * @param height the height of the boundary area (in pixels)
     * @return true if the cursor falls within the provided objects boundaries
     */
    public boolean hovered(float positionX, float positionY, float width, float height) {
        return (cursorPositionX > positionX && cursorPositionX < positionX + width) && 
               (cursorPositionY > positionY && cursorPositionY < positionY + height);
    }
    
    /**
     * Variant of {@linkplain hovered(float, float float float)} that instead 
     * takes a rectangle object.
     * 
     * @param bounds the boundaries of some interactive component on the UI
     * @return true if the cursor falls within the provided objects boundaries
     */
    public boolean hovered(Rectangle bounds) {
        return hovered(bounds.positionX, bounds.positionY, bounds.width, bounds.height);
    }
    
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
     * Used to determine whether or not a button on the mouse is currently 
     * being pressed over some UI element.
     * 
     * @param bounds the boundaries of some interactive component on the UI
     * @param buttonID a number provided by GLFW used to identify which button 
     *                 on the mouse was pressed
     * @return if true, the specified button on the mouse is being held
     */
    public boolean clicked(Rectangle bounds, int buttonID) {
        return hovered(bounds) && button == buttonID && currentClickValue;
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
     * Overloaded variant of {@linkplain clickedOnce()} that factors in the 
     * boundaries of a UI component and the ID of the mouse button used.
     * 
     * @param bounds the boundaries of some interactive component on the UI
     * @param buttonID a number provided by GLFW used to identify which button 
     *                 on the mouse was pressed
     * @return if true, the specified button on the mouse has been pressed
     */
    public boolean clickedOnce(Rectangle bounds, int buttonID) {
        return hovered(bounds) && button == buttonID && clickedOnce();
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