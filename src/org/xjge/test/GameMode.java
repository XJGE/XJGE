package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
abstract class GameMode {
    
    abstract void execute(SceneExplore scene, Map<UUID, Entity> entities);
    
}