package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.joml.Vector3i;
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
    
    private void attachUI(TurnContext turnContext) {
        if(UI.containsWidget(GLFW_JOYSTICK_1, "battle_actions")) UI.removeWidget(GLFW_JOYSTICK_1, "battle_actions");
        
        if(turnContext != null) {
            Entity unitEntity = turnContext.entities.values().stream()
                                                    .filter(entity -> entity.getAttribute(AttributeUnit.class) == turnContext.unit)
                                                    .findFirst().orElse(null);

            if(unitEntity != null) camera.follow(unitEntity);
            
            UI.addWidget(GLFW_JOYSTICK_1, "battle_actions", new WidgetBattle(turnContext));
        }
    }
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces) {
        if(!initialized) {
            entities.values().forEach(entity -> {
                if(entity.hasAttribute(AttributeUnit.class)) {
                    AttributeUnit unit = entity.getAttribute(AttributeUnit.class);
                    turnManager.queueUnit(unit);
                }
            });
            
            Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
            turnManager.startNextTurn(scene, entities, gridSpaces);
            attachUI(turnManager.getCurrentContext());
            
            initialized = true;
        }
        
        TurnContext turnContext = turnManager.getCurrentContext();
        
        if(turnContext != null) {
            turnManager.update();
            
            if(turnContext.isFinished()) {
                turnManager.startNextTurn(scene, entities, gridSpaces);
                attachUI(turnManager.getCurrentContext());
            }
        }
    }

}