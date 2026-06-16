package org.xjge.core;

/**
 * Created: May 12, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
record WidgetAddRequest (
    int viewportID,
    String name,
    Widget widget
) {}