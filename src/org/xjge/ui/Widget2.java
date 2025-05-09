package org.xjge.ui;

import java.util.Map;
import org.xjge.core.Viewport2;
import org.xjge.graphics.GLProgram;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public abstract class Widget2 {

    public abstract void render(Map<String, GLProgram> glPrograms, Viewport2 viewport);
    
    /*
    render(glPrograms, viewport) {
        font.drawString("bleh", 200, 200, Color.RED, 0.5f, viewport);
    
        viewport.width
        viewport.height
        viewport.near
        viewport.far
    }
    */
    
}