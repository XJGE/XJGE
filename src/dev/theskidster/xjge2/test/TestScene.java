package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.scene.Scene;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        entities.put("test", new TestEntity(0, 0, -5));
    }
    
    @Override
    public void update(double targetDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        GLProgram defaultProgram = glPrograms.get("default");
        defaultProgram.use();
        
        entities.values().forEach(entity -> entity.render(defaultProgram));
    }

    @Override
    public void exit() {
    }

}