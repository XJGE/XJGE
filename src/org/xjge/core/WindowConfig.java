package org.xjge.core;

/**
 * Created: May 28, 2025
 * <br><br>
 * Determines the initial configuration of the applications window.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public record WindowConfig(
    boolean resizable,
    int width,
    int height,
    String title,
    Monitor monitor,
    Resolution resolution
) {}