package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.GLProgram;
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
    public void update(double targetDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
    }

    @Override
    public void exit() {
    }

}