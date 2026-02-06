package org.xjge.core;

import java.util.LinkedList;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * TODO: temp file until we get everything moved out of the default shader
 * @author J Hoffman
 * @since 
 */
public final class ShaderDefault {

    private static ShaderDefault instance;
    private final GLProgram shader;
    
    private ShaderDefault() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_viewport_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_viewport_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "xjge_default");
    }
    
    public static ShaderDefault getInstance() {
        if(instance == null) instance = new ShaderDefault();
        return instance;
    }
    
    public void use() {
        shader.use();
    }
    
    public void setUniform(String name, float value) {
        shader.setUniform(name, value);
    }
    
    public void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }
    
    public void setUniform(String name, Vector2f value) {
        shader.setUniform(name, value);
    }
    
    public void setUniform(String name, Vector3f value) {
        shader.setUniform(name, value);
    }
    
    public void setUniform(String name, boolean transpose, Matrix4fc value) {
        shader.setUniform(name, transpose, value);
    }
    
}