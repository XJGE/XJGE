package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: May 11, 2021
 */

/**
 * Represents a way in which the screen can be divided during split screen mode.
 */
public enum Split {

    /**
     * The screen will not be divided. Only the first viewport will be visible.
     */
    NONE,
    
    /**
     * The screen will be divided horizontally from side to side. Only the first and second viewports will be visible and will appear to 
     * be stacked on top of one another.
     */
    HORIZONTAL,
    
    /**
     * The screen will be divided vertically from top to bottom. Only the first and second viewports will be visible and will appear to 
     * be positioned beside each other.
     */
    VERTICAL,
    
    /**
     * The screen will be divided evenly into three segments. Every viewport except the fourth will be visible.
     */
    TRISECT,
    
    /**
     * The screen will be divided evenly into quarters. All viewports will be visible.
     */
    QUARTER;
    
}