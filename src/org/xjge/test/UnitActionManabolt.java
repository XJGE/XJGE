package org.xjge.test;

import java.util.Random;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.Window;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionManabolt extends UnitAction {

    private boolean success;
    private boolean delayOver;
    
    private int stage;
    
    private final Timer fireDelay = new Timer();
    private final Timer finishDelay = new Timer();
    private final Random random = new Random();
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
                    Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    stage = 1;
                }
            }
            case 1 -> {
                if(widget.isFinished()) {
                    success = widget.getResult();
                    stage = 2;
                }
            }
            case 2 -> {
                if(!delayOver) {
                    //TODO: add particle effects
                    
                    if(fireDelay.tick(5, 12, true) && fireDelay.getTime() == 4) {
                        delayOver = true;
                    }
                } else {
                    WidgetDamage damageWidget;
                
                    if(success) {
                        damageWidget = new WidgetDamage(random.nextInt(2), "-3hp", Color.RED);
                        targetUnit.health -= 3;
                    } else {
                        damageWidget = new WidgetDamage(random.nextInt(2), "miss!", Color.WHITE);
                    }
                    
                    UI.addWidget(GLFW_JOYSTICK_1, "damage_" + damageWidget.uuid, damageWidget);

                    stage = 3;
                }
            }
            case 3 -> {
                if(finishDelay.tick(7, 12, true) && finishDelay.getTime() == 6) {
                    turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
                    Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    return true;
                }
            }
        }
        
        return false;
    }

}