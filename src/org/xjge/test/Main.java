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
    
    private static TCShowBoundingVolumes showBoundingVolumes;
    
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
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(new GLShader("shader_grid_vertex.glsl",   GL_VERTEX_SHADER));
                add(new GLShader("shader_grid_fragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram gridShader = new GLProgram(shaderSourceFiles, "grid");
            
            gridShader.addUniform(GLDataType.INT,  "uTexture");
            gridShader.addUniform(GLDataType.MAT4, "uModel");
            gridShader.addUniform(GLDataType.MAT4, "uView");
            gridShader.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("grid", gridShader);
        }
        
        Window.addObserver(new WindowObserver());
        
        //Window.setMinimumSize(640, 480);
        //Window.setSize(640, 480);
        //Window.setTitle("Test");
        //Window.setIcon("icon.png");
        //Window.setResolution(384, 216);
        Window.setResizable(false);
        //Window.setFullscreen(true);
        //Window.focus();
        
        showBoundingVolumes = new TCShowBoundingVolumes();
        XJGE.addCommand("showBoundingVolumes", showBoundingVolumes);
        
        XJGE.setScene(new Scene3D("map_test.txt"));
        XJGE.start(); //show window
        
    }
    
    public static boolean showBoundingVolumes() {
        return showBoundingVolumes.getValue();
    }
    
}