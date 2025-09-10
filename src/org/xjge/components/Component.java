package org.xjge.components;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public sealed interface Component permits 
        AIComponent, AxisComponent, ButtonComponent, 
        CursorComponent, ScrollWheelComponent {
    
    abstract float getInputValue();
    
}