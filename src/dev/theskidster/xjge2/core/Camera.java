package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

/**
 * Abstract class which can be used to create specialized camera objects for viewports. Subclasses of this object use a combination of 
 * matrices to alter how the game world is perceived.
 * <br><br>
 * NOTE: Game implementations that specify custom {@linkplain GLProgram shader programs} are required to supply a projection matrix of 
 * type mat4 via uniform variable if they wish to utilize camera objects correctly. More information about this can be found in the engines 
 * user manual under the section titled "Putting It All Into Perspective".
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
     * Creates a new camera object that will render the game world using the specified projection type. By default, the engine provides 
     * two projection types; orthographic for 2D and perspective for 3D.
     * 
     * @param isOrtho if true, the camera will use an orthogonal projection to render the scene
     */
    protected Camera(boolean isOrtho) {
        this.isOrtho = isOrtho;
        
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(isOrtho) setOrtho(glProgram, Window.getWidth(), Window.getHeight());
            else        setPerspective(glProgram, Window.getWidth(), Window.getHeight());
        });
    }
    
    /**
     * Performs an orthogonal transformation on the cameras projection matrix, effectively flattening the scene into 2D space.
     * 
     * @param glProgram the shader program that the value of the cameras projection matrix will be sent to through a uniform variable
     * @param width     the width of the viewport
     * @param height    the height of the viewport
     */
    void setOrtho(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setOrtho(0, width, 0, height, 0, Integer.MAX_VALUE);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * Performs a perspective transformation on the cameras projection matrix. This will cause the camera to render the current scene in 
     * full 3D.
     * 
     * @param glProgram the shader program that the value of the cameras projection matrix will be sent to through a uniform variable
     * @param width     the width of the viewport
     * @param height    the height of the viewport
     */
    void setPerspective(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * Optional abstract method which can be used to organize camera logic not directly related to rendering. Examples of this include 
     * applying effects such as camera shake or dolly motion.
     */
    public abstract void update();
    
    /**
     * Abstract method used to organize the cameras OpenGL calls and other rendering logic.
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * Changes the current projection type the camera will use.
     * 
     * @param isOrtho if true, the camera will use an orthogonal projection to render the scene
     */
    public void setProjectionType(boolean isOrtho) {
        this.isOrtho = isOrtho;
    }
    
}