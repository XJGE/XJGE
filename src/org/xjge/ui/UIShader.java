package org.xjge.ui;

import java.util.LinkedList;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.GLDataType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * Created: May 9, 2025
 * <br><br>
 * 
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
class UIShader {

    private static UIShader instance;
    private final GLProgram shader;
    
    private UIShader() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(new GLShader("shader_ui_vertex.glsl", GL_VERTEX_SHADER));
            add(new GLShader("shader_ui_fragment.glsl", GL_FRAGMENT_SHADER));
        }};

        shader = new GLProgram(shaderSourceFiles, "default");

        shader.use();
        shader.addUniform(GLDataType.INT,   "uType");
        shader.addUniform(GLDataType.INT,   "uTexture");
        shader.addUniform(GLDataType.INT,   "uIsBitmapFont");
        shader.addUniform(GLDataType.FLOAT, "uOpacity");
        shader.addUniform(GLDataType.VEC2,  "uTexCoords");
        shader.addUniform(GLDataType.VEC3,  "uColor");
        shader.addUniform(GLDataType.MAT4,  "uModel");
        shader.addUniform(GLDataType.MAT4,  "uProjection");
    }
    
    static UIShader getInstance() {
        if(instance == null) instance = new UIShader();
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
    
    void setUniform(String name, Vector2f value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, Vector3f value) {
        shader.setUniform(name, value);
    }
    
    void setUniform(String name, Matrix4fc value) {
        shader.setUniform(name, false, value);
    }
    
}