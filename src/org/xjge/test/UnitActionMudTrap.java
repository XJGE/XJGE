package org.xjge.test;

import java.util.List;
import org.joml.Vector3f;
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
    
    private Vector3f targetSpace;
    private WidgetMudToss widget;
    private final Timer throwDelay = new Timer();
    private final Timer landDelay = new Timer();
    private final Entity mudBall = new Entity();
    private final List<GridSpace> area;
    
    UnitActionMudTrap(List<GridSpace> area) {
        this.area = area;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
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
                    if(throwDelay.tick(5, 12, true) && throwDelay.getTime() == 4) {
                        delayOver = true;
                        
                        //Area is never empty as ensured by call in WidgetBattle
                        for(GridSpace space : area) {
                            if(space.target) {
                                targetSpace = new Vector3f(space.xLocation, space.yLocation, space.zLocation);
                                break;
                            }
                        }
                    }
                } else {
                    mudBall.getComponent(ComponentMudBall.class)
                           .launch(mudBall.getComponent(ComponentPosition.class).position, 
                                   targetSpace, 
                                   throwStrength);
                    
                    stage = 3;
                }
            }
            case 3 -> {
                turnContext.scene.focusOverheadCamera(mudBall, 1f);
                
                if(mudBall == null || mudBall.getComponent(ComponentMudBall.class).landed()) {
                    if(landDelay.tick(5, 12, true) && landDelay.getTime() == 4) {
                        turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

}