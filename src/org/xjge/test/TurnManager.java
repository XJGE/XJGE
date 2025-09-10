package org.xjge.test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.joml.Vector3i;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TurnManager {

    private TurnContext currentContext;
    
    private final Queue<AttributeUnit> queue = new LinkedList<>();
    
    private void endTurn() {
        if(currentContext != null) {
            queue.add(currentContext.unit);
            currentContext = null;
        }
    }
    
    void queueUnit(AttributeUnit unit) {
        queue.add(unit);
    }
    
    void startNextTurn(Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        if(queue.isEmpty()) return;
        AttributeUnit unit = queue.poll();
        currentContext = new TurnContext(unit, scene, entities, gridSpaces);
    }
    
    void update() {
        if(currentContext == null) return;
            ActionResult result = currentContext.executeActions();
        if(currentContext.isFinished() || result == ActionResult.FAILURE) {
            endTurn();
        }
    }
    
    TurnContext getCurrentContext() {
        return currentContext;
    }
    
}