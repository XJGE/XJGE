package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.UIManager;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class TestWidget extends Widget {

    private int count;
    
    private Rectangle bounds = new Rectangle(350, 350, 0, 0, Color.SILVER, 1f);
    
    TestWidget() {
        super(-1);
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(XJGE.tick(120)) {
            count++;
            if(count == 2) UIManager.bringToFront(GLFW_JOYSTICK_1, "layer_test");
        }
    }

    @Override
    public void render() {
        bounds.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        bounds.position.x = viewportWidth - bounds.width;
        bounds.position.y = viewportHeight - bounds.height;
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