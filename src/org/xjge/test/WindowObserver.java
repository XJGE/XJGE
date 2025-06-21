package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.xjge.core.Window;

/**
 * Created: Jun 14, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class WindowObserver implements PropertyChangeListener {
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "WINDOW_FULLSCREEN_CHANGED" -> {
                if(!((Boolean) evt.getNewValue())) {
                    Window.setSize(Window.DEFAULT_WIDTH, Window.DEFAULT_HEIGHT);
                    Window.center(Window.getMonitor());
                }
                
                Window.setResolution(Window.getWidth(), Window.getHeight());
            }
            
            case "WINDOW_RESOLUTION_WIDTH_CHANGED", "WINDOW_RESOLUTION_HEIGHT_CHANGED" -> {
                
            }
        }
    }
    
}