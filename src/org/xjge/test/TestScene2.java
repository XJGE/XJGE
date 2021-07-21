package org.xjge.test;

import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: Jun 27, 2021
 */

public class TestScene2 extends Scene {

    public TestScene2() {
        super("test scene 2");
        
        Game.setClearColor(Color.RED);
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