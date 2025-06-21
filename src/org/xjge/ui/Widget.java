package org.xjge.ui;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.graphics.GLProgram;

/**
 * 
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Widget {
    
    public abstract void update(double targetDelta, double trueDelta);
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight);
    
    public abstract void processKeyboardInput(int key, int action, int mods);
    
    public abstract void processMouseInput(Mouse mouse);
    
    public abstract void delete();
    
}