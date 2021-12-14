package org.xjge.scenes;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.core.Light;
import org.xjge.core.ShadowMap;
import org.xjge.core.XJGE;
import org.xjge.test.TestWidget;

/**
 * Dec 3, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class PlaneScene extends Scene {
    
    public PlaneScene() {
        super("plane");
        
        //Skybox skybox = new Skybox("sky_noon_top.png", "sky_noon_center.png", "sky_noon_bottom.png", true);
        //setSkybox(skybox);
        
        ShadowMap shadowMap = new ShadowMap();
        
        setShadowMap(shadowMap);
        
        Game.setClearColor(Color.BLACK);
        
        entities.put("plane", new EntityPlane(0, -3f, 0, 50, 50, Color.SILVER));
        entities.put("cube", new EntityCube(-6, 2, 0, 3, 3, 3, true));
        entities.put("teapot", new EntityTeapot(15, 3, -6));
        entities.put("triangle", new EntityBloom(2, 2.5f, -10, Color.WHITE, 1));
        
        Game.setClearColor(Color.BLACK);
        
        Light l1 = Light.random();
        Light l2 = Light.random();
        Light l3 = Light.random();
        Light l4 = Light.random();
        Light l5 = Light.random();
        
        l2.position.set(10, 0, 4);
        l3.position.set(20, 0, -17);
        l4.position.set(-16, 0, -3);
        l5.position.set(-2, 0, 10);
        
        lights[0] = Light.midnight();
        
        //lights[1] = l1;
        //lights[2] = l2;
        lights[3] = l3;
        //lights[4] = l4;
        //lights[5] = l5;
        
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "test", new TestWidget(0, 40, 0, 80, 80));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, depthTexHandle));
    }

    @Override
    public void exit() {
    }

}
