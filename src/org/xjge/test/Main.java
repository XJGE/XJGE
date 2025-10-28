package org.xjge.test;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLDataType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Main {

    public static void main(String[] args) {
        
        XJGE.init(true, "C:/Users/jhoff/Projects/XJGE/src/org/xjge/assets", "org.xjge.test.");
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(GLShader.load("shader_test_vertex.glsl", GL_VERTEX_SHADER));
                add(GLShader.load("shader_test_fragment.glsl", GL_FRAGMENT_SHADER));
            }};

            GLProgram testProgram = new GLProgram(shaderSourceFiles, "test");

            testProgram.use();
            testProgram.addUniform(GLDataType.INT,  "uTexture");
            testProgram.addUniform(GLDataType.MAT4, "uModel");
            testProgram.addUniform(GLDataType.MAT4, "uView");
            testProgram.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("test", testProgram);
        }
        
        XJGE.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}