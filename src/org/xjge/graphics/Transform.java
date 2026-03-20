package org.xjge.graphics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Transform extends EntityComponent {

    public final Vector3f position    = new Vector3f();
    public final Vector3f scale       = new Vector3f(1f);
    public final Quaternionf rotation = new Quaternionf();
    
}