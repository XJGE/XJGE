package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GameModeExplore extends GameMode {
    
    private boolean componentsAssigned;
    
    final CameraExplore camera = new CameraExplore();
    
    @Override
    void execute(SceneExplore scene, Map<UUID, Entity> entities) {
        if(!componentsAssigned) {
            entities.values().forEach(entity -> {
                if(entity.hasComponent(ComponentControllable.class)) {
                    entity.addComponent(new ComponentExplore(GLFW_JOYSTICK_1, entity, camera));
                }
            });
            
            componentsAssigned = true;
        }
    }

}