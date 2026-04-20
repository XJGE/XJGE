package org.xjge.core;

import org.xjge.graphics.Shader;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Created: May 8, 2021
 * <p>
 * Abstract class which can be used to create specialized objects that render 
 * the current perspective of a {@link Viewport}.
 * <p>
 * NOTE: Subclasses of this object use a combination of matrices to alter how 
 * the game world is perceived. Game implementations that specify custom 
 * {@link Shader shader programs} are required to supply a 4x4 
 * projection matrix through a uniform variable if they wish to utilize camera 
 * objects correctly.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Camera {
    
    public float fov = 45.0f;
    
    protected final Vector3f position  = new Vector3f();
    protected final Vector3f direction = new Vector3f(0, 0,-1);
    protected final Vector3f up        = new Vector3f(0, 1, 0);
    
    private final Matrix4f viewMatrix       = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();
    
    void buildViewMatrix() {
        buildViewMatrix(viewMatrix);
    }
    
    void buildProjectionMatrix(int viewportWidth, int viewportHeight) {
        buildProjectionMatrix(projectionMatrix, viewportWidth, viewportHeight);
    }
    
    /**
     * Used to organize camera orientation logic. Examples of this include applying effects such as camera shake or dolly motion.
     * Ultimately the values of the view and projection matrices should be calculated here so they can be passed to entities and 
     * objects that require them during rendering.
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    protected abstract void update(double targetDelta, double trueDelta);
    
    protected abstract void buildViewMatrix(Matrix4f viewMatrix);
    
    /**
     * 
     * @param projectionMatrix
     * @param viewportWidth the width (in pixels) of the viewport that's currently using this camera
     * @param viewportHeight the height (in pixels) of the viewport that's currently using this camera
     */
    protected abstract void buildProjectionMatrix(Matrix4f projectionMatrix, int viewportWidth, int viewportHeight);
    
    public Vector3fc getPosition() {
        return position;
    }
    
    public Vector3fc getDirection() {
        return direction;
    }
    
    public Vector3fc getUp() {
        return up;
    }
    
    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }
    
    public Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }
    
}