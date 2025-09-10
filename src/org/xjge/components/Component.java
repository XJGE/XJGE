package org.xjge.components;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public sealed interface Component permits 
        AxisComponent, ButtonComponent, CursorComponent, 
        ScrollWheelComponent, VirtualComponent {
    
    abstract float getInputValue();
    
}