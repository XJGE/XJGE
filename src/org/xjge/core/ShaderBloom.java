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
final class ShaderBloom {

    private static ShaderBloom instance;
    private final GLProgram shader;
    
    private ShaderBloom() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_blur_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_blur_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "xjge_bloom");
    }
    
    static ShaderBloom getInstance() {
        if(instance == null) instance = new ShaderBloom();
        return instance;
    }
    
    void use() {
        shader.use();
    }
    
    void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, Matrix4fc value) {
        shader.setUniform(name, false, value);
    }
    
}