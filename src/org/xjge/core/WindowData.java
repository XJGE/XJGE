package org.xjge.core;

/**
 * Created: Jun 4, 2025
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
    
    WindowData() {
        observable.properties.put("fullscreen", fullscreen);
        observable.properties.put("width", width);
        observable.properties.put("height", height);
        observable.properties.put("positionX", positionX);
        observable.properties.put("positionY", positionY);
        observable.properties.put("title", title);
        observable.properties.put("monitor", monitor);
        observable.properties.put("resolutionWidth", resolution.width);
        observable.properties.put("resolutionHeight", resolution.height);
        observable.properties.put("splitType", splitType);
    }
    
}