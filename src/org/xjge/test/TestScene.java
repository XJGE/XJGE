package org.xjge.test;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.UI;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    public TestScene() {
        super("test");
        
        Entity entity = new Entity();
        addEntity(entity);
        UI.addWidget(GLFW_JOYSTICK_1, "font_test", new TestWidget());
        
        //XJGE.setClearColor(Color.BLACK);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        //System.out.println("update");
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        //TODO: add test entity
        //System.out.println("render");
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}