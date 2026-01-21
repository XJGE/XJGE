package org.xjge.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EntityChangeRequest {

    boolean addEntity    = false;
    boolean removeEntity = false;
    
    UUID uuid;
    
    final Set<Class<? extends EntityComponent>> addComponents    = new HashSet<>();
    final Set<Class<? extends EntityComponent>> removeComponents = new HashSet<>();
    
    void applyChanges(boolean add, Entity entity, Map<UUID, EntitySignature> entitySignatures, 
                      Map<EntitySignature, List<Entity>> entityArchetypes, Class<? extends EntityComponent> subclass) {
        var oldSignature = entitySignatures.get(entity.uuid);
        if(oldSignature == null) return;

        var oldList = entityArchetypes.get(oldSignature);
        if(oldList != null) oldList.remove(entity);

        var nextSignature = oldSignature.copy();
        if(add) nextSignature.add(subclass);
        else    nextSignature.remove(subclass);

        var frozen = nextSignature.freeze();

        entitySignatures.put(entity.uuid, frozen);
        entityArchetypes.computeIfAbsent(frozen, k -> new ArrayList<>()).add(entity);
    }
    
}