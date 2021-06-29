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
 * Abstract class which can be used to create specialized camera objects for 
 * viewports. Subclasses of this object use a combination of matrices to alter 
 * how the game world is perceived.
 * <br><br>
 * NOTE: Game implementations that specify custom 
 * {@linkplain GLProgram shader programs} are required to supply a projection 
 * matrix of type mat4 via uniform variable if they wish to correctly utilize 
 * camera objects. More info about this can be found in the engines user manual.
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
     * specified projection type. By default, the engine provides two projection
     * types; orthographic for 2D and perspective for 3D.
     * 
     * @param isOrtho if true, the camera will utilize a 2D orthogonal projection 
     *                to render the scene, otherwise it will use a 3D perspective 
     *                projection
     */
    protected Camera(boolean isOrtho) {
        this.isOrtho = isOrtho;
        
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(isOrtho) setOrtho(glProgram, Window.getWidth(), Window.getHeight());
            else        setPerspective(glProgram, Window.getWidth(), Window.getHeight());
        });
    }
    
    /**
     * 
     * @param glProgram
     * @param width
     * @param height 
     */
    void setOrtho(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setOrtho(0, width, 0, height, 0, Integer.MAX_VALUE);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * 
     * @param glProgram
     * @param width
     * @param height 
     */
    void setPerspective(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    /**
     * 
     */
    public abstract void update();
    
    /**
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     */
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    /**
     * 
     * @return 
     */
    public int getFieldOfView() {
        return fov;
    }
    
    /**
     * 
     * @param fov 
     */
    public void setFieldOfView(int fov) {
        this.fov = fov;
    }
    
    /**
     * Changes the projection type used to render the scene.
     * 
     * @param isOrtho if true, the camera will utilize a 2D orthogonal projection 
     *                to render the scene, otherwise it will use a 3D perspective 
     *                projection
     */
    public void setProjectionType(boolean isOrtho) {
        this.isOrtho = isOrtho;
    }
    
}