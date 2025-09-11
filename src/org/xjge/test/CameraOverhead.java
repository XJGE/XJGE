package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {

    CameraOverhead() {
        super(false);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
    }

}