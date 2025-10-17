package org.xjge.test;

import java.util.Map;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetRules extends Widget {

    private Rectangle background = new Rectangle();
    private String[] text = new String[4];
    
    WidgetRules(String[] text) {
        this.text = text;
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        background.render(0.5f, Color.BLACK);
        
        for(int i = 0; i < 4; i++) {
            int yOffset = background.positionY + background.height - (25 * (i + 1));
            
            switch(i) {
                case 0 -> Font.fallback.drawString("MINIGAME RULES:", background.positionX + 10, yOffset, Color.CYAN, 1f);
                case 1 -> Font.fallback.drawString(text[0], background.positionX + 10, yOffset, Color.SILVER, 1f);
                case 2 -> Font.fallback.drawString(text[1], background.positionX + 10, yOffset, Color.SILVER, 1f);
                case 3 -> Font.fallback.drawString(text[2], background.positionX + 10, yOffset, Color.SILVER, 1f);
            }
        }
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        background.positionX = 420;
        background.positionY = 10;
        background.width = 350;
        background.height = 140;
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}