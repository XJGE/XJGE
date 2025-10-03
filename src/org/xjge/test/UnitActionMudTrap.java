package org.xjge.test;

import java.util.List;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Entity;
import org.xjge.core.Timer;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMudTrap extends UnitAction {

    private boolean delayOver;
    
    private float throwStrength;
    
    private int stage;
    
    private final Timer throwDelay = new Timer();
    private WidgetMudToss widget;
    private final Entity mudBall = new Entity();
    private final List<GridSpace> area;
    
    UnitActionMudTrap(List<GridSpace> area) {
        this.area = area;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        /*
        Lets implement it like this for now:
        
        Players hold the space/cross causing the mud ball to grow in size, once 
        the button is released the mud ball will be thrown to the target area. 
        If the button is held for too long it will burst covering the immediate 
        area surrounding the player. The size of the mud ball at the time of 
        launch determines its AOE.
        */
        
        switch(stage) {
            case 0 -> {
                mudBall.addComponent(new ComponentPosition(turnContext.unitPos.x, turnContext.unitPos.y + 1, turnContext.unitPos.z));
                mudBall.addComponent(new ComponentMudBall());
                
                turnContext.scene.addEntity(mudBall);
                turnContext.scene.focusOverheadCamera(mudBall, 0.02f);
                
                widget = new WidgetMudToss(turnContext.unit);
                UI.addWidget(GLFW_JOYSTICK_1, "mud_minigame", widget);
                
                stage = 1;
            }
            case 1 -> {
                if(widget.isFinished()) {
                    throwStrength = widget.getResult();
                    stage = 2;
                }
            }
            case 2 -> {
                if(!delayOver) {
                    if(throwDelay.tick(5, 15, true) && throwDelay.getTime() == 4) {
                        delayOver = true;
                    }
                } else {
                    System.out.println(throwStrength);
                    //TODO: detect mudball landed (posZ <= 0);
                }
            }
        }
        
        return false;
    }

}