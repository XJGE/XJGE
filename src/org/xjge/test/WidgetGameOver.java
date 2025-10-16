package org.xjge.test;

import java.util.Map;
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
class WidgetGameOver extends Widget {

    private int xPos;
    private int yPos;
    
    private final String info = "The game will restart shortly";
    private final String text;
    private final Color textColor;
    private final Rectangle background = new Rectangle();
    
    WidgetGameOver(String text, Color textColor) {
        this.text = text;
        this.textColor = textColor;
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        background.render(0.7f, Color.BLACK);
        Font.fallback.drawString(text, xPos - (Font.fallback.lengthInPixels(text) / 2), yPos + 10, textColor, 1f);
        Font.fallback.drawString(info, xPos - (Font.fallback.lengthInPixels(info) / 2), yPos - 10, Color.WHITE, 1f);
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        background.width  = viewportWidth;
        background.height = viewportHeight;
        xPos = viewportWidth / 2;
        yPos = viewportHeight / 2;
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