package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

public class TerminalOutput {
    
    public final String text;
    public final Color color;
    
    public TerminalOutput() {
        text  = "";
        color = Color.WHITE;
    }
    
    public TerminalOutput(String text, Color color) {
        this.text  = text;
        this.color = color;
    }

}