package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: Jun 28, 2021
 */

public class TestCam extends Camera {

    public TestCam() {
        super(true);
    }
    
    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            glProgram.use();
            
            viewMatrix.translation(Math.round(position.x), Math.round(position.y), 0);
            glProgram.setUniform("uView", false, viewMatrix);
        });
    }

}