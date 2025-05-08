package org.xjge.ui;

import java.util.LinkedList;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.GLDataType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * Created: May 8, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public class UI {

    private static UI instance;
    
    final GLProgram shader;
    
    private UI() {
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
    
    public static UI getInstance() {
        if(instance == null) instance = new UI();
        return instance;
    }
    
    public void setProjectionMatrix(int width, int height, int near, int far, Matrix4f projMatrix) {
        shader.use();
        projMatrix.setOrtho(0, width, 0, height, near, far);
        shader.setUniform("uProjection", false, projMatrix);
    }
    
}