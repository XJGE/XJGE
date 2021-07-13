package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.core.Song;
import dev.theskidster.xjge2.core.Sound;
import dev.theskidster.xjge2.core.Timer;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {

    Sound sound;
    Song song;
    TestCam camera = new TestCam();
    Timer timer = new Timer(6, 60);
    
    public TestScene() {
        super("test");
        
        //TestEntity entity = new TestEntity(0.5f, 0, 0, -10);
        
        //Input.setDevicePuppet(GLFW_JOYSTICK_1, entity.puppet);
        //Input.setDeviceDeadzone(0, 0.34f); //TODO: doesnt update cfg file.
        
        entities.put("test", new TestEntity3(0, 0, -5));
        //entities.put("test", entity);
        
        //XJGE.setViewportCamera(0, camera);
        
        //timer.start();
    }
    
    @Override
    public void update(double targetDelta) {
        /*
        if(timer != null) {
            timer.update();
            
            camera.setProjectionType(timer.time % 2 == 0);
            
            if(timer.time == 0) timer = null;
        }*/
        
        entities.values().forEach(entity -> entity.update(targetDelta));
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