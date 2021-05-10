package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

final class FreeCam extends Camera {

    private Vector3f tempFront = new Vector3f();
    
    FreeCam() {
        super(false);
    }

    @Override
    void update() {
    }

    @Override
    void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            glProgram.use();
            
            viewMatrix.setLookAt(position, position.add(direction, tempFront), up);
            glProgram.setUniform("uView", false, viewMatrix);
        });
    }

}