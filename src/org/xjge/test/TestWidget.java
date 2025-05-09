package org.xjge.test;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.ui.Font;
import org.xjge.core.Split;
import org.xjge.core.StopWatch;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Polygon;

/**
 * Created: Apr 25, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    Font font = new Font("font_source_code_pro.ttf", 32);
    StopWatch timer = new StopWatch();
    TextEffectTest testEffect = new TextEffectTest();
    Polygon polygon = new Polygon(6, true, 40, Color.ORANGE, 200, 200);
    
    int index;
    boolean reverse;
    
    public TestWidget() {
        super(new Vector3i(), Window.getWidth(), Window.getHeight());
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        polygon.render(0.5f);
        font.drawString("The quick brown fox jumps over the lazy dog.", 50, 100, testEffect);
    }

    @Override
    public void relocate(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(double cursorPosX, double cursorPosY, int button, int action, int mods, double scrollX, double scrollY) {
    }

    @Override
    public void destroy() {
    }

}