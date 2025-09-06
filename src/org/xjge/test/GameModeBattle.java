package org.xjge.test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Entity;
import org.xjge.core.UI;
import org.xjge.core.Window;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GameModeBattle extends GameMode {

    ComponentUnit activeUnit;
    Queue<ComponentUnit> turns = new LinkedList<>();
    
    CameraOverhead camera = new CameraOverhead();
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities) {
        if(!componentsAssigned) {
            entities.values().forEach(entity -> {
                ComponentUnit unit = (entity.hasComponent(ComponentUnit.class)) 
                                   ? entity.getComponent(ComponentUnit.class) 
                                   : null; 
                
                if(unit != null) entity.addComponent(unit);
            });
            
            Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
            componentsAssigned = true;
        }
        
        if(activeUnit != null && activeUnit.turnFinished(scene)) {
            activeUnit = turns.poll();
            
            if(UI.containsWidget(GLFW_JOYSTICK_1, "battle_actions")) UI.removeWidget(GLFW_JOYSTICK_1, "battle_actions");
            UI.addWidget(GLFW_JOYSTICK_1, "battle_actions", new WidgetBattle(activeUnit));
        }
        
        entities.forEach((uuid, entity) -> {
            if(entity != null && entity.hasComponent(ComponentUnit.class)) {
                ComponentUnit unit = entity.getComponent(ComponentUnit.class);
                
                if(unit != activeUnit && !turns.contains(unit)) {
                    if(activeUnit == null) {
                        activeUnit = unit;
                        UI.addWidget(GLFW_JOYSTICK_1, "battle_actions", new WidgetBattle(activeUnit));
                    }
                    camera.follow(entity);
                    turns.add(unit);
                }
            }
        });
    }

}