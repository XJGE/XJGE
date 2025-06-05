package org.xjge.core;

/**
 * Created: Jun 4, 2025
 * <br><br>
 * Contains mutable data that pertains to the applications window.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
final class WindowData {

    boolean fullscreen;
    
    int width;
    int height;
    int positionX;
    int positionY;
    
    String title;
    Monitor monitor;
    Resolution resolution;
    
    final Observable observable = new Observable(this);
    
    SplitScreenType splitType = SplitScreenType.NONE;
    
}