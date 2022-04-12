package org.xjge.main;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * Apr 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    public TestScene(String name) {
        super(name);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}
