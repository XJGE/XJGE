package org.xjge.test;

import org.joml.Matrix4f;
import org.xjge.core.Camera;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class CameraTransition extends Camera {

    private final Camera previous;
    private final Camera next;
    
    CameraTransition(Camera previous, Camera next) {
        this.previous = previous;
        this.next     = next;
    }
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
    }

    @Override
    protected void buildViewMatrix(Matrix4f viewMatrix) {
    }

    @Override
    protected void buildProjectionMatrix(Matrix4f projectionMatrix, int viewportWidth, int viewportHeight) {
    }

}