package org.xjge.main;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Apr 1, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene2 extends Scene {

    public TestScene2() {
        super("test2");
        
        Game.setClearColor(Color.RED);
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
