package org.xjge.core;

import java.util.ArrayList;
import java.util.List;
import org.xjge.graphics.Color;

/**
 * Created: May 25, 2021
 * <p>
 * A data structure which contains information pertaining to a commands 
 * response when executed.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class TerminalOutput {
    
    final String text;
    final Color color;
    
    final List<CharSequence> lines = new ArrayList<>();
    
    /**
     * Creates a response to show the user with the data provided.
     * 
     * @param text the text to show the user
     * @param color the color the text will be rendered in
     */
    public TerminalOutput(String text, Color color) {
        this.text  = "> " + text;
        this.color = color;
    }

}