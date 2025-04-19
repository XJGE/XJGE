package org.xjge.core;

/**
 * Created: May 11, 2022
 * <br><br>
 * Simple struct added during the 2.1.8 patch that is used to carry widget 
 * information through an asynchronous event queue. This solved an issue that 
 * would otherwise produce a {@link java.util.ConcurrentModificationException} 
 * at runtime.
 * 
 * @author J Hoffman
 * @since  2.1.8
 */
class WidgetAddEvent {
    
    int viewportID;
    boolean resolved;
    String name;
    Widget widget;
    
    /**
     * Collects information that will be utilized by an asynchronous event queue
     * within the {@link Game} class.
     * 
     * @param viewportID the ID number of the viewport to add the widget to
     * @param name the name that will be used to identify/remove the widget
     * @param widget the widget object to add
     */
    WidgetAddEvent(int viewportID, String name, Widget widget) {
        this.viewportID = viewportID;
        this.name       = name;
        this.widget     = widget;
    }

}