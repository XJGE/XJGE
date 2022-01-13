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

    Font font1;
    Font font2;
    Vector2i textPos1;
    Vector2i textPos2;
    
    public TestWidget() {
        super(new Vector3i(), 384, 216);
        
        font1 = new Font("fnt_dosmono.bmf", 8);
        font2 = new Font("fnt_expositsans.bmf", 8);
        textPos1 = new Vector2i(20, 60);
        textPos2 = new Vector2i(20, 20);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        drawString(font1, "The quick brown fox jumps over the lazy dog.", textPos1, Color.WHITE);
        drawString(font2, "The quick brown fox jumps over the lazy dog.", textPos2, Color.WHITE);
    }

    @Override
    public void setSplitPosition(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

}
