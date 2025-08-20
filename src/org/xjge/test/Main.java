package org.xjge.test;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLDataType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Main {
    
    public static void main(String args[]) {
        
        XJGE.init(true, "/org/xjge/assets/", "org.xjge.test.");
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(new GLShader("shader_test_vertex.glsl", GL_VERTEX_SHADER));
                add(new GLShader("shader_test_fragment.glsl", GL_FRAGMENT_SHADER));
            }};

            GLProgram testProgram = new GLProgram(shaderSourceFiles, "test");

            testProgram.use();
            testProgram.addUniform(GLDataType.INT,  "uTexture");
            testProgram.addUniform(GLDataType.MAT4, "uModel");
            testProgram.addUniform(GLDataType.MAT4, "uView");
            testProgram.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("test", testProgram);
        }
        
        Window.addObserver(new WindowObserver());
        
        //Window.setMinimumSize(640, 480);
        //Window.setSize(640, 480);
        //Window.setResizable(false);
        //Window.setTitle("Test");
        //Window.setIcon("icon.png");
        //Window.setResolution(384, 216);
        
        XJGE.setScene(new TestScene());
        
        XJGE.start(); //show window
        
    }
    
}