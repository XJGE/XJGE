package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {
    
    public TestScene() {
        super("test");
        
        Game.setClearColor(Color.create(92, 148, 252));
        
        entities.put("test", new TestEntity(0, 0, -5));
    }
    
    @Override
    public void update(double targetDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera) {
        GLProgram defaultProgram = glPrograms.get("default");
        defaultProgram.use();
        
        entities.values().forEach(entity -> entity.render(defaultProgram, camera));
    }

    @Override
    public void exit() {
    }

}