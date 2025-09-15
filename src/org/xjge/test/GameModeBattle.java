package org.xjge.test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Entity;
import org.xjge.core.UI;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GameModeBattle extends GameMode {
    
    private TurnContext currentContext;
    
    private final Queue<ComponentUnit> queue = new LinkedList<>();
    
    private void endTurn() {
        if(currentContext != null) {
            queue.add(currentContext.unit);
            currentContext = null;
        }
    }
    
    private void startNextTurn(Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        if(queue.isEmpty()) return;
        ComponentUnit unit = queue.poll();
        currentContext = new TurnContext(unit, scene, entities, gridSpaces);
        scene.setCameraFollow(unit, 0);
    }
    
    private void attachUI() {
        if(UI.containsWidget(GLFW_JOYSTICK_1, "battle_actions")) UI.removeWidget(GLFW_JOYSTICK_1, "battle_actions");
        if(currentContext != null) UI.addWidget(GLFW_JOYSTICK_1, "battle_actions", new WidgetBattle(currentContext));
    }
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        if(!initialized) {
            entities.values().forEach(entity -> {
                if(entity.hasComponent(ComponentUnit.class)) {
                    ComponentUnit unit = entity.getComponent(ComponentUnit.class);
                    queue.add(unit);
                }
            });
            
            startNextTurn(scene, entities, gridSpaces);
            attachUI();
            
            initialized = true;
        }
        
        if(currentContext != null) {
            ActionResult result = currentContext.executeActions();
            if(currentContext.isFinished() || result == ActionResult.FAILURE) endTurn();
            
            if(currentContext.isFinished()) {
                startNextTurn(scene, entities, gridSpaces);
                attachUI();
            }
        }
    }

}