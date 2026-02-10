package org.xjge.core;

import java.util.LinkedList;
import org.joml.Matrix4fc;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class ShaderScene {

    private static ShaderScene instance;
    private final GLProgram shader;
    
    private ShaderScene() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_viewport_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_scene_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "xjge_viewport");
    }
    
    static ShaderScene getInstance() {
        if(instance == null) instance = new ShaderScene();
        return instance;
    }
    
    void use() {
        shader.use();
    }
    
    void setUniform(String name, float value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, Matrix4fc value) {
        shader.setUniform(name, false, value);
    }
    
}