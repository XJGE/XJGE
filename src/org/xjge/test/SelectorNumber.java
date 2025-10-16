package org.xjge.test;

import java.util.Random;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class SelectorNumber {
    
    private int stage;
    
    private WidgetCrystalBall ballWidget;
    private WidgetDamage damageWidget;
    private final Timer timer = new Timer();
    private final Random random = new Random();
    
    int prompt(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                ballWidget = new WidgetCrystalBall(turnContext);
                UI.addWidget(GLFW_JOYSTICK_1, "number_selector", ballWidget);
                stage = 1;
            }
            case 1 -> {
                if(ballWidget.finished() && timer.tick(3, 30, false) && timer.getTime() == 0) {
                    damageWidget = new WidgetDamage(random.nextInt(2), "+" + ballWidget.result() + "hp", Color.GREEN);
                    UI.addWidget(GLFW_JOYSTICK_1, "damage_" + damageWidget.uuid, damageWidget);
                    UI.removeWidget(GLFW_JOYSTICK_1, "number_selector");
                    return ballWidget.result();
                }
            }
        }
        
        return -1;
    }
    
}