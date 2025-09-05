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
    
    final CameraOverhead camera = new CameraOverhead();
    
    @Override
    void execute(Scene3D scene, Map<UUID, Entity> entities) {
        if(!componentsAssigned) {
            entities.values().forEach(entity -> {
                if(entity.hasComponent(ComponentUnit.class)) {
                    entity.addComponent(new ComponentExplore(GLFW_JOYSTICK_1, entity, camera));
                }
            });
            
            componentsAssigned = true;
        }
    }

}