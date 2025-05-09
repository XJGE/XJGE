package org.xjge.core;

import org.joml.Matrix4f;

/**
 * Created: May 9, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public class UIContext {

    private static final Matrix4f projectionMatrix = new Matrix4f();
    
    static void updateProjectionMatrix(int width, int height, int near, int far) {
        projectionMatrix.setOrtho(0, width, 0, height, near, far);
    }
    
    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    
}