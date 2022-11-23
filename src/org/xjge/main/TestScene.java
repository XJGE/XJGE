package org.xjge.main;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 *
 * @author thesk
 */
public class TestScene extends Scene {

    TestScene() {
        super("test");
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "test widget", new TestWidget());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        //System.out.println(XJGE.containsWidget(GLFW_JOYSTICK_1, "test widget"));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}
