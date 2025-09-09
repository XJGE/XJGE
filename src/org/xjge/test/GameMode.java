package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.joml.Vector3i;
import org.xjge.core.Entity;

/**
 * 
 * @author J Hoffman
 * @since 
 */
abstract class GameMode {
    
    protected boolean initialized;
    
    abstract void execute(Scene3D scene, Map<UUID, Entity> entities, Map<Vector3i, GridSpace> gridSpaces);
    
}