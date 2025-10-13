package org.xjge.test;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.UI;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetMudToss extends Widget {

    private boolean goingUp = true;
    private boolean stopped;
    private boolean blink;
    private boolean finished;
    
    private int tickCount;
    
    private float result;
    private float speed = 8f;
    
    private final ComponentUnit unit;
    
    private Rectangle background = new Rectangle(0, 0, 50, 300);
    private Rectangle indicator = new Rectangle(0, 0, 30, 0);
    
    WidgetMudToss(ComponentUnit unit) {
        this.unit = unit;
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(unit.buttonPressedOnce(Control.CROSS, 0)) {
            stopped = true;
            result = indicator.height / 280f;
        }
        
        if(!stopped) {
            if(indicator.height <= 0) {
                goingUp = true;
            } else if(indicator.height >= 280) {
                goingUp = false;
            }

            if(goingUp) {
                indicator.height += speed;
            } else {
                indicator.height -= speed;
            }
        } else {
            if(XJGE.tick(3)) {
                blink = !blink;
                tickCount++;
                
                if(tickCount > 10) {
                    finished = true;
                    UI.removeWidget(GLFW_JOYSTICK_1, "mud_minigame");
                }
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        background.render(1f, Color.BLACK);
        indicator.render(1f, !blink ? Color.WHITE : Color.SILVER);
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        background.positionX = (viewportWidth / 2) + 200;
        background.positionY = viewportHeight / 2 - background.height / 2;
        
        indicator.positionX = background.positionX + 10;
        indicator.positionY = background.positionY + 10;
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }
    
    boolean isFinished() {
        return finished;
    }
    
    float getResult() {
        return result;
    }

}