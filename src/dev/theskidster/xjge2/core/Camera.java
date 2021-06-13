package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

public abstract class Camera {
    
    protected int fov = 45;
    
    public final boolean ortho;
    
    public Vector3f position  = new Vector3f();
    public Vector3f direction = new Vector3f(0, 0, -1);
    public Vector3f up        = new Vector3f(0, 1, 0);
    
    protected Matrix4f viewMatrix = new Matrix4f();
    protected Matrix4f projMatrix = new Matrix4f();
    
    protected Camera(boolean ortho) {
        this.ortho = ortho;
        
        /*
        Implementing applications will need to supply a projection matrix via 
        shader uniform variable if they wish to utilize camera objects.
        */
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(ortho) setOrtho(glProgram, Window.getWidth(), Window.getHeight());
            else      setPerspective(glProgram, Window.getWidth(), Window.getHeight());
        });
    }
    
    abstract void update();
    
    abstract void render(Map<String, GLProgram> glPrograms);
    
    public int getFieldOfView() {
        return fov;
    }
    
    public void setFieldOfView(int fov) {
        this.fov = fov;
    }
    
    public void setOrtho(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setOrtho(0, width, 0, height, 0, Integer.MAX_VALUE);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
    public void setPerspective(GLProgram glProgram, int width, int height) {
        glProgram.use();
        projMatrix.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
        glProgram.setUniform("uProjection", false, projMatrix);
    }
    
}