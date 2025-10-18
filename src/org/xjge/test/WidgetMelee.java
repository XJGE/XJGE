package org.xjge.test;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Timer;
import org.xjge.core.UI;
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
class WidgetMelee extends Widget {

    private int tickCount;
    private int offsetY;
    
    private Color textColor = Color.WHITE;
    private final Timer timer = new Timer();
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(timer.tick(5)) {
            textColor = (tickCount % 2 == 0) ? Color.WHITE : Color.BLACK;
            tickCount++;
        }
        
        offsetY += 2;
        
        if(tickCount > 6) UI.removeWidget(GLFW_JOYSTICK_1, "melee_qte");
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        int halfWidth = Font.fallback.lengthInPixels("[PRESS SPACE/CIRCLE]") / 2;
        Font.fallback.drawString("[PRESS SPACE/CIRCLE]", 
                                 (Window.getResolutionWidth() / 2) - halfWidth, 
                                 (Window.getResolutionHeight() - 200) + offsetY, 
                                 textColor, 1f);
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
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