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
    
    
    - Viewport currently owns and maintains the projection matrix
    
    - We could have the UI classes (font, rectangle, etc) keep their own copy and
      update it using the width/height values of the current viewport by passing
      the viewport object like above
    
    - Alternatively we could make the widget maintain the projection matrix and 
      alter it any time the viewports size is changed, it would always be ortho 
      this way
    */
    
}