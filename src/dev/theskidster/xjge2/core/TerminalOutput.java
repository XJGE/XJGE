package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

/**
 * A data structure which contains information pertaining to a commands response when executed.
 */
public class TerminalOutput {
    
    public final String text;
    public final Color color;
    
    /**
     * Creates an empty response.
     */
    public TerminalOutput() {
        text  = "";
        color = Color.WHITE;
    }
    
    /**
     * Creates a response to show the user with the data provided.
     * 
     * @param text  the text to show the user
     * @param color the color the text will be rendered in
     */
    public TerminalOutput(String text, Color color) {
        this.text  = text;
        this.color = color;
    }

}