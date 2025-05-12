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
        boolean validViewportID = viewportID > -1 && viewportID < 4;
        boolean validName       = name != null && !name.isEmpty();
        boolean validWidget     = widget != null;
        
        if(validViewportID && validName && validWidget) {
            widgetAddQueue.add(new WidgetAddRequest(viewportID, name, widget));
            if(XJGE.debugEnabled()) Logger.logInfo("Added widget \"" + name + "\" to viewport " + viewportID);
        } else {
            String reason;
            
            if(!validViewportID) reason = "Invalid ID used (found: " + viewportID + " expected: 0-3)";
            else if(!validName)  reason = "Name cannot be an empty string or null";
            else                 reason = "Widget object was not initialized";
            
            Logger.logInfo("Failed to add widget to viewport. " + reason);
        }
    }
    
    public static final void removeWidget(int viewportID, String name) {
        boolean validViewportID = viewportID > -1 && viewportID < 4;
        boolean validName       = name != null && !name.isEmpty();
        boolean validPresence   = (validViewportID && validName) ? widgets.get(viewportID).containsKey(name) : false;
        
        if(validViewportID && validName && validPresence) {
            widgetRemoveQueue.add(new WidgetRemoveRequest(viewportID, name));
        } else {
            String reason;
            
            if(!validViewportID) reason = "Invalid ID used (found: " + viewportID + " expected: 0-3)";
            else if(!validName)  reason = "Name cannot be an empty string or null";
            else                 reason = "Viewport " + viewportID + " does not contain a widget with the name \"" + name + "\"";
            
            Logger.logInfo("Failed to remove widget from viewport. " + reason);
        }
    }
    
    public static final void clearWidgets(int viewportID) {
        
    }
    
    public static boolean containsWidget(int viewportID, String name) {
        return false;
    }
    
    public static Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }
    
}