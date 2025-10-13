package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionManabolt extends UnitAction {

    private boolean success;
    
    private int stage;
    
    private final ComponentUnit targetUnit;
    private WidgetManabolt widget;
    
    UnitActionManabolt(ComponentUnit targetUnit) {
        this.targetUnit = targetUnit;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                if(widget == null) {
                    widget = new WidgetManabolt(turnContext);
                    UI.addWidget(GLFW_JOYSTICK_1, "manabolt_minigame", widget);
                }
            }
            case 1 -> {
                if(widget.isFinished()) {
                    success = widget.getResult();
                    stage = 2;
                }
            }
            case 2 -> {
                if(success) {
                    //hit!
                } else {
                    //miss
                }
            }
        }
        
        return false;
    }

}