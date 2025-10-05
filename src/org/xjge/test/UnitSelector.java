package org.xjge.test;

import java.util.List;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitSelector {

    private int stage;
    private int index;
    
    private Entity arrowIndicator;
    private final List<Entity> units;
    
    UnitSelector(List<Entity> units) {
        this.units = units;
    }
    
    ComponentUnit prompt(TurnContext turnContext) {
        Entity selectedEntity = units.get(index);
        
        switch(stage) {
            case 0 -> {
                turnContext.scene.setCameraOverhead(0.5f);
                Vector3f targetPos = turnContext.getUnitPos(selectedEntity.getComponent(ComponentUnit.class));
                turnContext.scene.snapOverheadCamera(targetPos.x, targetPos.y, targetPos.z);
                arrowIndicator = new Entity().addComponent(new ComponentUnitSelect())
                                             .addComponent(new ComponentPosition(0, 0, 0));
                turnContext.scene.addEntity(arrowIndicator);
                stage = 1;
            }
            case 1 -> {
                if(selectedEntity != null) {
                    turnContext.scene.focusOverheadCamera(selectedEntity, 0.02f);
                    
                    //TODO: handle input for cycling targets
                    if(turnContext.unit.buttonPressedOnce(Control.CROSS)) {
                        return selectedEntity.getComponent(ComponentUnit.class);
                    }
                }
            }
        }
        
        return null;
    }
    
}