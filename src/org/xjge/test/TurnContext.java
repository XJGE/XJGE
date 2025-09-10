package org.xjge.test;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.joml.Vector3i;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TurnContext {

    private boolean finished;
    
    final AttributeUnit unit;
    final Scene3D scene;
    final Map<UUID, Entity> entities;
    final Map<Vector3i, GridSpace> gridSpaces;
    
    private final EnumMap<ActionCategory, UnitAction> chosenActions = new EnumMap<>(ActionCategory.class);
    
    TurnContext(AttributeUnit unit, Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        this.unit = unit;
        this.scene = scene;
        this.entities = entities;
        this.gridSpaces = gridSpaces;
        //TODO: might be better to provide entities and grid info from Scene3D object
    }
    
    boolean addAction(ActionCategory category, UnitAction action) {
        if(chosenActions.containsKey(category)) return false;
        chosenActions.put(category, action);
        return true;
    }
    
    boolean isFinished() {
        return finished;
    }
    
    ActionResult executeActions() {
        if(chosenActions.isEmpty()) return ActionResult.PENDING;
        
        for(var entry : chosenActions.entrySet()) {
            ActionResult result = entry.getValue().perform(this);
            
            if(result == ActionResult.PENDING || result == ActionResult.TRIGGER_RTR) {
                return result;
            }
        }
        
        finished = true;
        return ActionResult.SUCCESS;
    }
    
}