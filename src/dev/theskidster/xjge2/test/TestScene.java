package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.Light;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {
    
    Light light;
    
    public TestScene() {
        super("test");
        
        light = new Light(1, 1, new Vector3f(-1, 0, -3), Color.random(), Color.WHITE);
        addLight(light);
        
        addLightAtIndex(0, Light.daylight());
        
        entities.put("test",  new TestEntity(0, 0, -3));
        //entities.put("test2", new TestEntity2(2, 1, -3));
        entities.put("test3", new TestEntity3(6, 0, -10));
    }
    
    @Override
    public void update(double targetDelta) {
        
        //light.position.x += 0.1f;
        
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