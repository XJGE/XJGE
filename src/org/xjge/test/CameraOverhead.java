package org.xjge.test;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.xjge.core.Camera;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class CameraOverhead extends Camera {
    
    private final Vector3f tempFront = new Vector3f();
    
    CameraOverhead(float posX, float posY, float posZ, float dirX, float dirY, float dirZ) {
        position.set(posX, posY, posZ);
        direction.set(dirX, dirY, dirZ);
    }

    @Override
    protected void update(double targetDelta, double trueDelta) {
        
    }

    @Override
    protected void buildViewMatrix(Matrix4f viewMatrix) {
        viewMatrix.setLookAt(position, tempFront.set(direction).add(position), up);
    }

    @Override
    protected void buildProjectionMatrix(Matrix4f projectionMatrix, int viewportWidth, int viewportHeight) {
        projectionMatrix.setPerspective((float) Math.toRadians(fov), 
                                        (float) viewportWidth / viewportHeight, 0.1f, Float.POSITIVE_INFINITY);
    }

}