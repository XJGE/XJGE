package org.xjge.graphics;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class JointRig extends EntityComponent {

    private final Map<String, JointAttachment> attachments = new LinkedHashMap<>();
    
    public void add(String name, JointAttachment joint) {
        attachments.put(name, joint);
    }
    
    public void remove(String name) {
        attachments.remove(name);
    }
    
    public void update(Transform transform, ModelAnimator animator) {
        for(var joint : attachments.values()) joint.update(transform, animator);
    }
    
    public JointAttachment get(String name) {
        return attachments.get(name);
    }
    
    public Collection<JointAttachment> getAll() {
        return attachments.values();
    }
    
}