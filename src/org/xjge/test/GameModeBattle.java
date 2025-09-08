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
    
    private final TurnManager turnManager = new TurnManager();
    private final CameraOverhead camera = new CameraOverhead();
    
    private void attachUI(TurnContext turnContext, Map<UUID, Entity> entities) {
        if(UI.containsWidget(GLFW_JOYSTICK_1, "battle_actions")) UI.removeWidget(GLFW_JOYSTICK_1, "battle_actions");
        
        if(turnContext != null) {
            Entity unitEntity = entities.values().stream()
                                        .filter(entity -> entity.getComponent(ComponentUnit.class) == turnContext.unit)
                                        .findFirst().orElse(null);

            if(unitEntity != null) camera.follow(unitEntity);
            
            UI.addWidget(GLFW_JOYSTICK_1, "battle_actions", new WidgetBattle(turnContext.unit));
        }
    }
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities) {
        if(!initialized) {
            entities.values().forEach(entity -> {
                if(entity.hasComponent(ComponentUnit.class)) {
                    ComponentUnit unit = entity.getComponent(ComponentUnit.class);
                    turnManager.queueUnit(unit);
                }
            });
            
            Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
            turnManager.startNextTurn(scene, entities);
            attachUI(turnManager.getCurrentContext(), entities);
            
            initialized = true;
        }
        
        TurnContext turnContext = turnManager.getCurrentContext();
        
        if(turnContext != null) {
            turnManager.update();
            
            if(turnContext.isFinished()) {
                turnManager.startNextTurn(scene, entities);
                attachUI(turnManager.getCurrentContext(), entities);
            }
        }
    }

}