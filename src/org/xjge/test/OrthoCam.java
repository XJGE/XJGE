package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class OrthoCam extends Camera {

    private float orthoNear = Short.MIN_VALUE;
    private float orthoFar  = Short.MAX_VALUE;
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
    }

    @Override
    protected void render(Map<String, GLProgram> glPrograms, int width, int height) {
        
        projMatrix.setOrtho(0, width, 0, height, orthoNear, orthoFar);
        
        glPrograms.forEach((name, program) -> {
            System.out.println(name);
        });
    }

}