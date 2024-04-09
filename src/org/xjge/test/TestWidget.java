package org.xjge.test;

//Created: Apr 9, 2024

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    public TestWidget() {
        super(new Vector3i(), 200, 200);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
    }

    @Override
    public void relocate(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(double cursorPosX, double cursorPosY, int button, int action, int mods, double scrollX, double scrollY) {
        System.out.println(cursorPosX + " " + cursorPosY + " " + button + " " + action + " " + mods + " " + scrollX + " " + scrollY);
    }

    @Override
    public void destroy() {
    }

}
