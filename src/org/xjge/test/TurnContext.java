package org.xjge.test;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TurnContext {

    private boolean finished;
    
    final Vector3f unitPos;
    final ComponentUnit unit;
    final Scene3D scene;
    final Map<UUID, Entity> entities;
    final Map<Vector3i, GridSpace> gridSpaces;
    
    private final EnumMap<ActionCategory, UnitAction> chosenActions = new EnumMap<>(ActionCategory.class);
    
    TurnContext(ComponentUnit unit, Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        this.unit = unit;
        this.scene = scene;
        this.entities = entities;
        this.gridSpaces = gridSpaces;
        
        unitPos = getUnitPos(unit);
        
        //TODO: might be better to provide entities and grid info from Scene3D object
    }
    
    void endTurn() {
        finished = true; //Finish turn early
    }
    
    boolean addAction(ActionCategory category, UnitAction action) {
        if(chosenActions.containsKey(category)) return false;
        chosenActions.put(category, action);
        return true;
    }
    
    boolean executeActions() {
        chosenActions.entrySet().removeIf(entry -> entry.getValue().perform(this));
        return finished;
    }
    
    final Vector3f getUnitPos(ComponentUnit unit) {
        return entities.values().stream()
                       .filter(entity -> entity.hasComponent(ComponentUnit.class) 
                                   && entity.getComponent(ComponentUnit.class) == unit)
                       .map(entity -> entity.getComponent(ComponentPosition.class).position)
                       .findFirst()
                       .orElse(null);
    }
    
}