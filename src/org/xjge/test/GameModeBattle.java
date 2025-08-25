package org.xjge.test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GameModeBattle extends GameMode {

    ComponentUnit activeUnit;
    Queue<ComponentUnit> turns = new LinkedList<>();
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities) {
        if(activeUnit != null && activeUnit.turnFinished(scene)) activeUnit = turns.poll();
        
        entities.forEach((uuid, entity) -> {
            if(entity != null && entity.hasComponent(ComponentUnit.class)) {
                ComponentUnit unit = entity.getComponent(ComponentUnit.class);
                
                if(unit != activeUnit && !turns.contains(unit)) {
                    if(activeUnit == null) activeUnit = unit;
                    scene.camera.setActiveUnit(activeUnit);
                    turns.add(unit);
                }
            }
        });
    }

}