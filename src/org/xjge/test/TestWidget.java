package org.xjge.test;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Font2;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Created: Apr 25, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    Font2 font = new Font2("font_source_code_pro.ttf", 24);
    
    public TestWidget() {
        super(new Vector3i(), Window.getWidth(), Window.getHeight());
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        //font.drawString("bleh", 100, 100, Color.WHITE, 1f);
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