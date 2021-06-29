package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.core.Song;
import dev.theskidster.xjge2.core.Sound;
import dev.theskidster.xjge2.core.Timer;
import dev.theskidster.xjge2.core.XJGE;
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
    Timer timer = new Timer(3, 60);
    
    public TestScene() {
        super("test");
        
        entities.put("test", new TestEntity(30, 30, -50));
        
        timer.start();
    }
    
    @Override
    public void update(double targetDelta) {
        if(timer != null) {
            timer.update();
        
            if(timer.time == 0) {
                XJGE.setViewportCamera(0, camera);
                timer = null;
            }
        }
        
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