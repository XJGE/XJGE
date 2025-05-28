package org.xjge.core;

/**
 * Created: May 28, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public record WindowConfig(
    boolean resizable,
    int width,
    int height,
    String title,
    Monitor monitor
) {}