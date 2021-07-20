package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

//Created: May 8, 2021

/**
 * Abstract class which can be used to create specialized objects that will 
 * render the current perspective of a {@link Viewport}.
 * <p>
 * NOTE: Subclasses of this object use a combination of matrices to alter how 
 * the game world is perceived. Game implementations that specify custom 
 * {@linkplain GLProgram shader programs} are required to supply a 4x4 
 * projection matrix of via uniform variable if they wish to utilize camera 
 * objects correctly. More information about this can be found in the engines 
 * user manual under the section titled "Putting It All Into Perspective".
 * </p>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Camera {
    
    protected int fov = 45;
    
    boolean isOrtho;
    
    public Vector3f position  = new Vector3f();
    public Vector3f direction = new Vector3f(0, 0, -1);
    public Vector3f up        = new Vector3f(0, 1, 0);
    
    protected Matrix4f viewMatrix = new Matrix4f();
    protected Matrix4f projMatrix = new Matrix4f();
    
    /**
     * Creates a new camera object that will render the game world using the 
     * specified projection type. By default, the engine provides two 
     * projection types; orthographic for 2D and perspective for 3D.
     * 
     * @param isOrtho if true, the camera will use an orthographic projection 
     *                to render the scene
     */
    protected Camera(boolean isOrtho) {
        this.isOrtho = isOrtho;
        
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(isOrtho) setOrtho(glProgram, Window.getWidth(), Window.getHeight());
            else        setPerspective(glProgram, Window.getWidth(), Window.getHeight());
        });
    }
    
    /**
     * Performs an orthographic transformation on the cameras projection matrix, 
     * effectively flattening the game world into 2D space.
     * 
     * @param glProgram the shader program that the value of the cameras 
     *                  projection matrix will be sent to through a uniform 
     *                  variable
     * @param width     the width of the viewport (in pixels)
     * @param height    the height of the viewport (in pixels)
     */
    void setOrtho(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setOrtho(0, width, 0, height, 0, Integer.MAX_VALUE);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * Performs a perspective transformation on the cameras projection matrix. 
     * This will cause the camera to render the game world in full 3D.
     * 
     * @param glProgram the shader program that the value of the cameras 
     *                  projection matrix will be sent to through a uniform 
     *                  variable
     * @param width     the width of the viewport (in pixels)
     * @param height    the height of the viewport (in pixels)
     */
    void setPerspective(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * Optional abstract method which can be used to organize camera logic not 
     * directly related to rendering. Examples of this include applying effects
     * such as camera shake or dolly motion.
     */
    public abstract void update();
    
    /**
     * Used to organize calls to the OpenGL API and other code pertaining to
     * rendering.
     * 
     * @param glPrograms an immutable collection containing the shader programs
     *                   compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * Changes the current projection type the camera will use. 
     * <p>
     * By default, the engine provides two projection types; orthographic for 
     * 2D and perspective for 3D.
     * </p>
     * 
     * @param isOrtho if true, the camera will use an orthographic projection 
     *                to render the scene
     */
    public void setProjectionType(boolean isOrtho) {
        this.isOrtho = isOrtho;
    }
    
}