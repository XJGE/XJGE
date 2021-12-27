package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Light;
import org.xjge.core.Scene;
import org.xjge.core.ShadowMap;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        XJGE.setViewportCamera(GLFW_JOYSTICK_1, new Camera2D());
        
        entities.put("test1", new TestEntity(40, 40, 0, 16));
        entities.put("test2", new TestEntity(48, 48, 3, 16));       
        
        lights[0] = new Light(1, 0.55f, 1, new Vector3f(-10, 30, 40), Color.WHITE);
        setShadowMap(new ShadowMap(0.00003f, 0.0009f, 100f, 100f, true, 1920, 1920, true, 0));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, depthTexHandle));
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
        entities.values().forEach(entity -> entity.renderShadow(depthProgram));
    }

    @Override
    public void exit() {
    }

}
