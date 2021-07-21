package org.xjge.test;

import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.Song;
import org.xjge.core.Sound;
import org.xjge.core.Timer;
import org.xjge.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import org.xjge.core.Widget;
import org.xjge.core.XJGE;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {

    Sound sound;
    Song song;
    TestCam camera = new TestCam();
    Timer timer = new Timer(6, 60);
    
    TestWidget widg1;
    TestWidget widg2;
    
    public TestScene() {
        super("test");
        
        entities.put("test", new TestEntity3(0, 0, -5));
        //entities.put("test", entity);
        
        //XJGE.setViewportCamera(0, camera);
        
        /*
        widg1 = new TestWidget(new Vector3i(), XJGE.getResolutionX(), XJGE.getResolutionY());
        widg2 = new TestWidget(new Vector3i(), XJGE.getResolutionX(), XJGE.getResolutionY());
        
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "w1", widg1);
        XJGE.addUIWidget(GLFW_JOYSTICK_2, "w2", widg2);
        */
        
        //timer.start();
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        /*
        if(timer != null) {
            timer.update();
            
            camera.setProjectionType(timer.time % 2 == 0);
            
            if(timer.time == 0) timer = null;
        }*/
        
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        GLProgram defaultProgram = glPrograms.get("default");
        defaultProgram.use();
        
        entities.values().forEach(entity -> entity.render(defaultProgram, camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}