package org.xjge.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public class UIContext {

    private static final Matrix4f projectionMatrix = new Matrix4f();
    
    private static final Queue<WidgetAddRequest> widgetAddQueue       = new LinkedList<>();
    private static final Queue<WidgetRemoveRequest> widgetRemoveQueue = new LinkedList<>();
    
    private static final Map<Integer, Map<String, Widget>> widgets = new HashMap<>();
    
    static {
        for(int i = 0; i < 4; i++) widgets.put(i, new HashMap<>());
    }
    
    private static String failureReason(boolean[] validation, int viewportID, String name) {
        int errorCode = 0;
        
        for(int i = 0; i < 4; i++) {
            if(!validation[i]) {
                errorCode = i + 1;
                break;
            }
        }
        
        return switch(errorCode) {
            default -> "";
            case 1  -> "Invalid ID used (found: " + viewportID + " expected: 0-3)";
            case 2  -> "Name cannot be an empty string or null";
            case 3  -> "Widget object was not initialized";
            case 4  -> "Viewport " + viewportID + " does not contain a widget with the name \"" + name + "\"";
        };
    }
    
    static void updateProjectionMatrix(int width, int height, int near, int far) {
        projectionMatrix.setOrtho(0, width, 0, height, near, far);
    }
    
    static void updateWidgets() {
        
    }
    
    static void renderWidgets() {
        
    }
    
    static void relocateWidgets() {
        
    }
    
    static void processKeyboardInput() {
        
    }
    
    static void processMouseInput() {
        
    }
    
    static void processAddRequests() {
        if(!widgetAddQueue.isEmpty()) {
            WidgetAddRequest request = widgetAddQueue.poll();
            widgets.get(request.viewportID()).put(request.name(), request.widget());
        }
    }
    
    static void processRemoveRequests() {
        if(!widgetRemoveQueue.isEmpty()) {
            WidgetRemoveRequest request = widgetRemoveQueue.poll();
            widgets.get(request.viewportID()).remove(request.name());
        }
    }
    
    public static final void addWidget(int viewportID, String name, Widget widget) {
        boolean[] validation = {
            viewportID > -1 && viewportID < 4,
            name != null && !name.isEmpty(),
            widget != null,
            true
        };
        
        if(validation[0] && validation[1] && validation[2]) {
            widgetAddQueue.add(new WidgetAddRequest(viewportID, name, widget));
            if(XJGE.debugEnabled()) Logger.logInfo("Added widget \"" + name + "\" to viewport " + viewportID);
        } else {
            Logger.logInfo("Failed to add widget to viewport. " + failureReason(validation, viewportID, name));
        }
    }
    
    public static final void removeWidget(int viewportID, String name) {
        boolean[] validation = {
            viewportID > -1 && viewportID < 4,
            name != null && !name.isEmpty(),
            true,
            widgets.get(viewportID).containsKey(name)
        };
        
        if(validation[0] && validation[1] && validation[3]) {
            widgetRemoveQueue.add(new WidgetRemoveRequest(viewportID, name));
        } else {
            Logger.logInfo("Failed to remove widget from viewport. " + failureReason(validation, viewportID, name));
        }
    }
    
    public static final void clearWidgets(int viewportID) {
        
    }
    
    public static boolean containsWidget(int viewportID, String name) {
        boolean validViewportID = viewportID > -1 && viewportID < 4;
        boolean validName       = name != null && !name.isEmpty();
        
        if(validViewportID && validName) {
            return widgets.get(viewportID).containsKey(name);
        } else {
            String reason;
            
            if(!validViewportID) reason = "Invalid ID used (found: " + viewportID + " expected: 0-3)";
            else                 reason = "Name cannot be an empty string or null";
            
            Logger.logInfo("" + reason);
            
            return false;
        }
    }
    
    public static Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }
    
}