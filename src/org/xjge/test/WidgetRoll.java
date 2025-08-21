package org.xjge.test;

import java.util.Map;
import org.joml.Vector2i;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Font;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetRoll extends Widget {
    
    private final int roll;
    
    private final Vector2i textPos = new Vector2i();
    
    WidgetRoll(int roll) {
        this.roll = roll;
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        textPos.x = Window.getResolutionWidth() / 2;
        textPos.y = Window.getResolutionHeight() - 60;
        
        Font.fallback.drawString(roll + "", textPos.x, textPos.y, Color.RED, 1f);
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