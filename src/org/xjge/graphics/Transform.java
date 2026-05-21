package org.xjge.graphics;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
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
    
    private final Matrix4f modelMatrix = new Matrix4f();
    
    public Transform() {}
    
    public Transform(float positionX, float positionY, float positionZ) {
        position.set(positionX, positionY, positionZ);
    }
    
    public Matrix4fc getModelMatrix() {
        return modelMatrix.identity().translate(position).rotate(rotation).scale(scale);
    }
    
}