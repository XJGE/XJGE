package org.xjge.test;

import java.util.Random;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Input;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.Window;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionUseItem extends UnitAction {
    
    private int stage;
    private final int subMenuChoice;
    
    private WidgetCrystalBall ballWidget;
    private WidgetDamage damageWidget;
    private final Timer timer = new Timer();
    private final Random random = new Random();
    
    UnitActionUseItem(int subMenuChoice) {
        this.subMenuChoice = subMenuChoice;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                ballWidget = new WidgetCrystalBall(turnContext);
                UI.addWidget(GLFW_JOYSTICK_1, "number_selector", ballWidget);
                turnContext.scene.ignoreFollowCameraInput(true);
                Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                
                String[] text = (turnContext.unit.inputDeviceID == Input.KEYBOARD) 
                              ? new String[] {"Wave the wand using the", "[CURSOR] then select", "a number with [LEFT CLICK]", ""} 
                              : new String[] {"Wave the wand using the", "[RIGHT STICK] then select", "a number with [CROSS]", ""};
                UI.addWidget(GLFW_JOYSTICK_1, "minigame_rules", new WidgetRules(text));
                
                stage = 1;
            }
            case 1 -> {
                if(ballWidget.finished() && timer.tick(3, 30, false) && timer.getTime() == 0) {
                    if(turnContext.unit.health + ballWidget.result() > turnContext.unit.maxHealth) {
                        turnContext.unit.health = turnContext.unit.maxHealth;
                    } else {
                        turnContext.unit.health += ballWidget.result();
                    }
                    
                    damageWidget = new WidgetDamage(random.nextInt(2), "+" + ballWidget.result() + "hp", Color.GREEN);
                    UI.addWidget(GLFW_JOYSTICK_1, "damage_" + damageWidget.uuid, damageWidget);
                    UI.removeWidget(GLFW_JOYSTICK_1, "number_selector");
                    UI.removeWidget(GLFW_JOYSTICK_1, "minigame_rules");
                    Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    turnContext.scene.ignoreFollowCameraInput(false);
                    turnContext.unit.items.remove(subMenuChoice);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
}