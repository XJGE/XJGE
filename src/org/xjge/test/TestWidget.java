package org.xjge.test;

import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Dec 8, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    private Vector2i textPos = new Vector2i();
    
    public TestWidget(int x, int y, int z, int width, int height) {
        super(new Vector3i(x, y, z), width, height);
    }

    @Override
    public void update() {
        textPos.x = position.x;
        textPos.y = position.y;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.get("default").use();
        drawString(defaultFont, "bleh", textPos, Color.WHITE);
    }

    @Override
    public void setSplitPosition(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

}
