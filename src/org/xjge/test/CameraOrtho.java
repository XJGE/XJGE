package org.xjge.test;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.xjge.core.Camera;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class CameraOrtho extends Camera {

    private float orthoNear = Short.MIN_VALUE;
    private float orthoFar  = Short.MAX_VALUE;
    
    private final Vector3f tempFront = new Vector3f();

    @Override
    protected void update(double targetDelta, double trueDelta) {
    }

    @Override
    protected void buildViewMatrix(Matrix4f viewMatrix) {
        viewMatrix.setLookAt(position, tempFront.set(direction).add(position), up);
    }

    @Override
    protected void buildProjectionMatrix(Matrix4f projectionMatrix, int viewportWidth, int viewportHeight) {
        projectionMatrix.setOrtho(0, viewportWidth, 0, viewportHeight, orthoNear, orthoFar);
    }
    
    

}