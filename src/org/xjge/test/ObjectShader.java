package org.xjge.test;

import java.util.LinkedList;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.XJGE;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class ObjectShader {

    private static ObjectShader instance;
    private final Shader shader;
    
    private ObjectShader() {
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("shader_object_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("shader_object_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new Shader(shaderSourceFiles, "test_object");
        XJGE.addGLProgram("test_cube", shader);
    }
    
    static ObjectShader getInstance() {
        if(instance == null) instance = new ObjectShader();
        return instance;
    }
    
    void use() {
        shader.use();
    }
    
    void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, Vector3fc value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, boolean transpose, Matrix3fc value) {
        shader.setUniform(name, transpose, value);
    }
    
    void setUniform(String name, boolean transpose, Matrix4fc value) {
        shader.setUniform(name, transpose, value);
    }
    
}