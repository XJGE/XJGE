package org.xjge.core;

/**
 * Created: Apr 9, 2024
 * <p>
 * Stores the current input values of the mouse and supplies them to the 
 * {@link Widget#processMouseInput(double, double, int, int, int, double, double)} 
 * method.
 * 
 * @author J Hoffman
 * @since  2.4.6
 */
public final class Mouse {

    
    
    double cursorX;
    double cursorY;
    double scrollX;
    double scrollY;
    
    int button;
    int action;
    int mods;
    
    public boolean hovered() {
        return false;
    }
    
}