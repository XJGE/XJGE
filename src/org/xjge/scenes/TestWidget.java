package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.xjge.core.Font;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Jan 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    Font font;
    Vector2i textPos;
    
    public TestWidget() {
        super(new Vector3i(), 384, 216);
        
        font    = new Font("fnt_expositsans.bmf", 1);
        textPos = new Vector2i(20, 20);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        drawString(font, "The quick brown fox jumps over the lazy dog.", textPos, Color.WHITE);
    }

    @Override
    public void setSplitPosition(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

}
