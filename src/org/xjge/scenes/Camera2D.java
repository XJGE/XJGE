package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.graphics.GLProgram;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Camera2D extends Camera {

    public Camera2D() {
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