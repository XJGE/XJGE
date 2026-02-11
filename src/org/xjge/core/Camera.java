package org.xjge.core;

import org.xjge.graphics.GLProgram;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

/**
 * Created: May 8, 2021
 * <p>
 * Abstract class which can be used to create specialized objects that render 
 * the current perspective of a {@link Viewport}.
 * <p>
 * NOTE: Subclasses of this object use a combination of matrices to alter how 
 * the game world is perceived. Game implementations that specify custom 
 * {@linkplain GLProgram shader programs} are required to supply a 4x4 
 * projection matrix through a uniform variable if they wish to utilize camera 
 * objects correctly.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Camera {
    
    public float fov = 45.0f;
    
    public Vector3f position  = new Vector3f();
    public Vector3f direction = new Vector3f(0, 0, 1);
    public Vector3f up        = new Vector3f(0, 1, 0);
    
    protected Matrix4f viewMatrix = new Matrix4f();
    protected Matrix4f projMatrix = new Matrix4f();
    
    /**
     * Optional abstract method which can be used to organize camera logic not 
     * directly related to rendering. Examples of this include applying effects
     * such as camera shake or dolly motion.
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    protected abstract void update(double targetDelta, double trueDelta);
    
    /**
     * Used to organize calls to the OpenGL API and other code pertaining to
     * rendering. More specifically, this is where you should upload the new 
     * value of your view matrix to the graphics card.
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     * @param width the width (in pixels) of the viewport that's currently using this camera
     * @param height the heigth (in pixels) of the viewport that's currently using this camera
     */
    protected abstract void render(Map<String, GLProgram> glPrograms, int width, int height);
    
    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }
    
    public Matrix4fc getProjectionMatrix() {
        return projMatrix;
    }
    
}