package org.xjge.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class UIManager {

    private static final int[] topLayer = new int[4];
    
    private static final boolean[] renderOrderDirty = new boolean[4];
    
    private static final Matrix4f projectionMatrix = new Matrix4f();
    
    private static final Queue<WidgetAddRequest> widgetAddQueue       = new LinkedList<>();
    private static final Queue<WidgetRemoveRequest> widgetRemoveQueue = new LinkedList<>();
    
    private static final Map<Integer, Map<String, Widget>> widgets = new HashMap<>();
    private static final Map<Integer, List<Widget>> renderOrder    = new HashMap<>();
    
    static {
        for(int i = 0; i < 4; i++) {
            widgets.put(i, new HashMap<>());
            renderOrder.put(i, new ArrayList<>());
        }
    }
    
    private static int findTopLayer(int viewportID) {
        int result = 0;
        
        for(Widget widget : widgets.get(viewportID).values()) {
            if(widget.layer > result) result = widget.layer;
        }
        
        return result;
    }
    
    private static String validateInput(int viewportID, String name) {
        if(viewportID < 0 || viewportID > 3)    return "Invalid ID used (found: " + viewportID + " required: 0-3)";
        else if(name == null || name.isEmpty()) return "Name cannot be an empty string or null";
        else return null;
    }
    
    static void updateProjectionMatrix(int width, int height, int near, int far) {
        projectionMatrix.setOrtho(0, width, 0, height, near, far);
    }
    
    static void updateWidgets(int viewportID, double targetDelta, double trueDelta) {
        for(Widget widget : widgets.get(viewportID).values()) widget.update(targetDelta, trueDelta);
    }
    
    static void renderWidgets(int viewportID) {
        if(renderOrderDirty[viewportID]) {
            var list = renderOrder.get(viewportID);
            
            list.clear();
            list.addAll(widgets.get(viewportID).values());
            list.sort(Comparator.comparingInt(Widget::getLayer));
            topLayer[viewportID] = findTopLayer(viewportID);
            
            renderOrderDirty[viewportID] = false;
        }
        
        for(Widget widget : renderOrder.get(viewportID)) widget.render();
    }
    
    static void relocateWidgets(int viewportID, int viewportWidth, int viewportHeight) {
        for(Widget widget : widgets.get(viewportID).values()) {
            widget.relocate(Window.getSplitScreenType(), viewportWidth, viewportHeight);
        }
    }
    
    static void processKeyboardInput(int key, int action, int mods, Character character) {
        //TODO: only listens on viewport 0 for now, extend this to all that are currently active.
        widgets.get(0).values().forEach(widget -> {
            widget.processKeyboardInput(key, action, mods, character);
        });
    }
    
    static void processMouseInput(Mouse mouse) {
        widgets.get(0).values().forEach(widget -> widget.processMouseInput(mouse));
    }
    
    static void processWidgetAddRequests() {
        while(!widgetAddQueue.isEmpty()) {
            WidgetAddRequest request = widgetAddQueue.poll();
            widgets.get(request.viewportID()).put(request.name(), request.widget());
            renderOrderDirty[request.viewportID()] = true;
            
            Logger.logInfo("Added widget \"" + request.name() + "\" to viewport " + request.viewportID());
        }
    }
    
    static void processWidgetRemoveRequests() {
        while(!widgetRemoveQueue.isEmpty()) {
            WidgetRemoveRequest request = widgetRemoveQueue.poll();
            var viewportWidgets = widgets.get(request.viewportID());
            if(request.delete()) viewportWidgets.get(request.name()).delete();
            viewportWidgets.remove(request.name());
            renderOrderDirty[request.viewportID()] = true;
            
            Logger.logInfo("Removed widget \"" + request.name() + "\" from viewport " + request.viewportID());
        }
    }
    
    public static void setWidgetLayer(int viewportID, String name, int layer) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && !widgets.get(viewportID).containsKey(name)) {
            failureReason = "Viewport " + viewportID + " does not contain a widget by the name \"" + name + "\"";
        }
        
        if(failureReason == null) {
            var widget = widgets.get(viewportID).get(name);
        
            if(widget.layer != layer) {
                widget.layer = layer;
                renderOrderDirty[viewportID] = true;
            }
        } else {
            Logger.logWarning("Failed to change the rendering layer for " + name +  ". " + failureReason, null);
        }
    }
    
    public static void bringToFront(int viewportID, String name) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && !widgets.get(viewportID).containsKey(name)) {
            failureReason = "Viewport " + viewportID + " does not contain a widget by the name \"" + name + "\"";
        }
        
        if(failureReason == null) {
            int newLayer = topLayer[viewportID] + 1;
            var widget   = widgets.get(viewportID).get(name);
            widget.layer         = newLayer;
            topLayer[viewportID] = newLayer;
            renderOrderDirty[viewportID] = true;
        } else {
            Logger.logWarning("Failed to change the rendering layer for " + name +  ". " + failureReason, null);
        }
    }
    
    public static final void addWidget(int viewportID, String name, Widget widget) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && widget == null) failureReason = "Widget object was not initialized";
        
        if(failureReason == null) {
            widgetAddQueue.add(new WidgetAddRequest(viewportID, name, widget));
        } else {
            Logger.logWarning("Failed to add widget to viewport " + viewportID + ". " + failureReason, null);
        }
    }
    
    public static final void removeWidget(int viewportID, String name, boolean delete) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && !widgets.get(viewportID).containsKey(name)) {
            failureReason = "Viewport " + viewportID + " does not contain a widget with the name \"" + name + "\"";
        }
        
        if(failureReason == null) {
            widgetRemoveQueue.add(new WidgetRemoveRequest(viewportID, name, delete));
        } else {
            Logger.logWarning("Failed to remove widget from viewport " + viewportID + ". " + failureReason, null);
        }
    }
    
    public static final void clearWidgets(int viewportID, boolean delete) {
        if(viewportID > -1 && viewportID < 4) {
            if(delete) widgets.get(viewportID).values().forEach(Widget::delete);
            widgets.get(viewportID).clear();
            renderOrderDirty[viewportID] = true;
        } else {
            Logger.logWarning("Invalid ID used (found: " + viewportID + " required: 0-3)", null);
        }
    }
    
    public static boolean containsWidget(int viewportID, String name) {
        String failureReason = validateInput(viewportID, name);
        
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