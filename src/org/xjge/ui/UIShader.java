package org.xjge.ui;

import java.util.LinkedList;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * Encapsulates a shader program used exclusively for rendering the engines 
 * default UI utilities.
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class UIShader {

    private static UIShader instance;
    private final GLProgram shader;
    
    /**
     * Compiles and links the associated vertex/fragment shader stages used by 
     * the UI for rendering.
     */
    private UIShader() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_ui_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_ui_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "default");
    }
    
    /**
     * Obtains the singleton instance of this class for internal use.
     * 
     * @return the object that represents the UI shader program
     */
    static UIShader getInstance() {
        if(instance == null) instance = new UIShader();
        return instance;
    }
    
    /**
     * Sets this as the current shader program the GPU will use for all 
     * subsequent rendering operations.
     */
    void use() {
        shader.use();
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    void setUniform(String name, float value) {
        shader.setUniform(name, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    void setUniform(String name, Vector2fc value) {
        shader.setUniform(name, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    void setUniform(String name, Vector3fc value) {
        shader.setUniform(name, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    void setUniform(String name, Matrix4fc value) {
        shader.setUniform(name, false, value);
    }
    
}