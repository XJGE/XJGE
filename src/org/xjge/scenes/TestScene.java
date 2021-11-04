package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Oct 31, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        //Game.setClearColor(Color.BLACK);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
    }

    @Override
    public void exit() {
    }

}
