package org.xjge.test;

import java.util.Map;
import org.xjge.core.Control;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetManabolt extends Widget {
    
    private final TurnContext turnContext;
    private Texture targetTexture;
    private Texture markerTexture;
    private Icon targetIcon;
    private Icon markerIcon;
    
    WidgetManabolt(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        targetTexture = new Texture("image_target.png");
        targetIcon = new Icon(targetTexture, 100, 100, true);
        targetIcon.scale.set(2);
        
        markerTexture = new Texture("image_marker.png");
        markerIcon = new Icon(markerTexture, 50, 50, true);
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 1)) {
                markerIcon.position.x = turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 1);
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 1)) {
                markerIcon.position.y = turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 1);
            }
        } else {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                markerIcon.position.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0);
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                markerIcon.position.y += turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0);
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        targetIcon.render();
        markerIcon.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        targetIcon.position.set(viewportWidth / 2, viewportHeight / 2);
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }

}