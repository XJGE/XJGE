package org.xjge.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Widget;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class UI {

    private static final Matrix4f projectionMatrix = new Matrix4f();
    
    private static final Queue<WidgetAddRequest> widgetAddQueue       = new LinkedList<>();
    private static final Queue<WidgetRemoveRequest> widgetRemoveQueue = new LinkedList<>();
    
    private static final Map<Integer, Map<String, Widget>> widgets = new HashMap<>();
    
    static {
        for(int i = 0; i < 4; i++) widgets.put(i, new HashMap<>());
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
    
    static void renderWidgets(int viewportID, Map<String, GLProgram> glPrograms) {
        for(Widget widget : widgets.get(viewportID).values()) widget.render(glPrograms);
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
            
            Logger.logInfo("Added widget \"" + request.name() + "\" to viewport " + request.viewportID());
        }
    }
    
    static void processWidgetRemoveRequests() {
        while(!widgetRemoveQueue.isEmpty()) {
            WidgetRemoveRequest request = widgetRemoveQueue.poll();
            var viewportWidgets = widgets.get(request.viewportID());
            viewportWidgets.get(request.name()).delete();
            viewportWidgets.remove(request.name());
            
            Logger.logInfo("Removed widget \"" + request.name() + "\" from viewport " + request.viewportID());
        }
    }
    
    public static final void addWidget(int viewportID, String name, Widget widget) {
        String failureReason = validateInput(viewportID, name);
        
        if(failureReason == null && widget == null) failureReason = "Widget object was not initialized";
        
        if(failureReason == null) {
            widgetAddQueue.add(new WidgetAddRequest(viewportID, name, widget));
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
    
    /**
     * Utility method that finds the total number of times the provided 
     * character appears in a string of text.
     * 
     * @param text the string of text to evaluate
     * @param character the character to search for
     * @param index a number denoting which index the search will start from
     * @return the number of times the character appears in the string
     */
    public static int numCharOccurences(CharSequence text, char character, int index) {
        if(index >= text.length()) return 0;
        int count = (text.charAt(index) == character) ? 1 : 0;
        return count + numCharOccurences(text, character, index + 1);
    }
    
    public static Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }
    
}