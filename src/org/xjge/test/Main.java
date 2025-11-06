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
        
        XJGE.init(true, "C:\\Users\\jhoff\\OneDrive\\Desktop\\assets", "org.xjge.test.");
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(GLShader.load("shader_test_vertex.glsl", GL_VERTEX_SHADER));
                add(GLShader.load("shader_test_fragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            XJGE.addGLProgram("test", new GLProgram(shaderSourceFiles, "test"));
        }
        
        XJGE.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}