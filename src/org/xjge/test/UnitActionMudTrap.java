package org.xjge.test;

import java.util.List;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMudTrap extends UnitAction {

    private int stage;
    
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
                mudBall.addComponent(new ComponentPosition(turnContext.unitPos.x, turnContext.unitPos.y, turnContext.unitPos.z));
                mudBall.addComponent(new ComponentMudBall());
                
                turnContext.scene.addEntity(mudBall);
                
                turnContext.scene.focusOverheadCamera(mudBall, 0.02f);
                
                stage = 1;
            }
            case 1 -> {
                
            }
        }
        
        return false;
    }

}