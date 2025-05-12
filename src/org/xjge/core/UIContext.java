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
    
    private static String validateInput(int viewportID, String name) {
        if(viewportID < 0 || viewportID > 3)    return "Invalid ID used (found: " + viewportID + " expected: 0-3)";
        else if(name == null || name.isEmpty()) return "Name cannot be an empty string or null";
        else return null;
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
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && widget == null) failureReason = "Widget object was not initialized";
        
        if(failureReason == null) {
            widgetAddQueue.add(new WidgetAddRequest(viewportID, name, widget));
            if(XJGE.debugEnabled()) Logger.logInfo("Added widget \"" + name + "\" to viewport " + viewportID);
        } else {
            Logger.logWarning("Failed to add widget to viewport. " + failureReason, null);
        }
    }
    
    public static final void removeWidget(int viewportID, String name) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && !widgets.get(viewportID).containsKey(name)) {
            failureReason = "Viewport " + viewportID + " does not contain a widget with the name \"" + name + "\"";
        }
        
        if(failureReason == null) {
            widgetRemoveQueue.add(new WidgetRemoveRequest(viewportID, name));
        } else {
            Logger.logWarning("Failed to remove widget from viewport. " + failureReason, null);
        }
    }
    
    public static final void clearWidgets(int viewportID) {
        if(viewportID > -1 && viewportID < 4) {
            widgets.get(viewportID).clear();
        } else {
            Logger.logWarning("Invalid ID used (found: " + viewportID + " expected: 0-3)", null);
        }
    }
    
    public static boolean containsWidget(int viewportID, String name) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && !widgets.get(viewportID).containsKey(name)) {
            failureReason = "Viewport " + viewportID + " does not contain a widget with the name \"" + name + "\"";
        }
        
        if(failureReason == null) {
            return widgets.get(viewportID).containsKey(name);
        } else {            
            Logger.logWarning(failureReason, null);
            return false;
        }
    }
    
    public static Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }
    
}