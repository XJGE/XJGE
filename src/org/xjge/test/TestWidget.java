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

    private boolean toggle;
    
    private Rectangle bounds = new Rectangle(0, 0, 350, 350);
    
    TestWidget() {
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(XJGE.tick(120)) {
            toggle = !toggle;
            UIManager.setWidgetLayer(GLFW_JOYSTICK_1, "layer_test", (toggle) ? 1 : -1);
        }
    }

    @Override
    public void render() {
        bounds.render(Color.SILVER, 1f);
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        bounds.positionX = viewportWidth - bounds.width;
        bounds.positionY = viewportHeight - bounds.height;
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