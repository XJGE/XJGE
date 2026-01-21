package org.xjge.core;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: move this into the Scene class?
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EntityComponentRegistry {

    private static int nextID = 0;
    
    private static final Map<Class<?>, Integer> ids = new HashMap<>();
    
    static int id(Class<?> type) {
        return ids.computeIfAbsent(type, t -> nextID++);
    }
    
}