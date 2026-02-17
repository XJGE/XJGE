package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.xjge.core.Window;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class WindowObserver implements PropertyChangeListener {
    
    WindowObserver subscribe() {
        Window.addObserver(this);
        return this;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "WINDOW_FULLSCREEN_CHANGED" -> {
                if(!((Boolean) evt.getNewValue())) {
                    Window.setSize(Window.DEFAULT_WIDTH, Window.DEFAULT_HEIGHT);
                    Window.center(Window.getMonitor());
                }
            }
            case "WINDOW_WIDTH_CHANGED", "WINDOW_HEIGHT_CHANGED" -> {
                Window.setResolution(Window.getWidth(), Window.getHeight());
            }
        }
    }
    
}