package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class SelectorNumber {
    
    private int stage;
    
    private WidgetCrystalBall widget;
    
    int prompt(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                widget = new WidgetCrystalBall(turnContext);
                UI.addWidget(GLFW_JOYSTICK_1, "number_selector", widget);
                stage = 1;
            }
            case 1 -> {
                
            }
        }
        
        return -1;
    }
    
}