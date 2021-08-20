package org.xjge.test;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;

/**
 * Aug 19, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        //Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        
    }

    @Override
    public void exit() {
        
    }

}
