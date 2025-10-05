package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionManabolt extends UnitAction {

    private final ComponentUnit targetUnit;
    private WidgetManabolt widget;
    
    UnitActionManabolt(ComponentUnit targetUnit) {
        this.targetUnit = targetUnit;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        if(widget == null) {
            widget = new WidgetManabolt(turnContext);
            UI.addWidget(GLFW_JOYSTICK_1, "manabolt_minigame", widget);
        }
        
        return false;
    }

}