package org.xjge.test;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.ui.Font;
import org.xjge.core.SplitScreenType;
import org.xjge.core.StopWatch;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Polygon;
import org.xjge.ui.Widget;

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
    
    public TestWidget() {}

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        polygon.render(0.5f);
        font.drawString("The quick brown fox jumps\nover the lazy dog.", 50, 100, testEffect);
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}