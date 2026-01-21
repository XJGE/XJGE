package org.xjge.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A compact, comparable description of which components and entity has.
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EntitySignature {

    private final Set<Class<? extends EntityComponent>> components;
    
    EntitySignature() {
        this.components = new HashSet<>();
    }
    
    private EntitySignature(Set<Class<? extends EntityComponent>> components) {
        this.components = components;
    }
    
    void add(Class<? extends EntityComponent> component) {
        components.add(component);
    }
    
    void remove(Class<? extends EntityComponent> component) {
        components.remove(component);
    }
    
    boolean containsAll(EntitySignature other) {
        return components.containsAll(other.components);
    }
    
    EntitySignature copy() {
        return new EntitySignature(new HashSet<>(components));
    }
    
    EntitySignature freeze() {
        return new EntitySignature(Collections.unmodifiableSet(components));
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof EntitySignature other)) return false;
        return components.equals(other.components);
    }

    @Override
    public int hashCode() {
        return components.hashCode();
    }
    
}