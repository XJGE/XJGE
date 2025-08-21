package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.joml.Vector3i;

/**
 * 
 * @author J Hoffman
 * @since 
 */
abstract class Action {

    abstract boolean perform(Scene3D scene, ComponentUnit activeUnit, Map<UUID, ComponentUnit> units, Map<Vector3i, GridSpace> spaces);
    
}