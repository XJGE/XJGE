package org.xjge.test;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
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
    private static TCSetGameMode setGameMode;
    
    public static void main(String args[]) {
        
        XJGE.init(true, "/org/xjge/assets/", "org.xjge.test.");
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(new GLShader("shader_grid_vertex.glsl", GL_VERTEX_SHADER));
                add(new GLShader("shader_grid_fragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram gridShader = new GLProgram(shaderSourceFiles, "grid");
            
            gridShader.addUniform(GLDataType.INT,  "uTexture");
            gridShader.addUniform(GLDataType.MAT4, "uModel");
            gridShader.addUniform(GLDataType.MAT4, "uView");
            gridShader.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("grid", gridShader);
        }
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(new GLShader("shader_volume_vertex.glsl", GL_VERTEX_SHADER));
                add(new GLShader("shader_volume_fragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram volumeShader = new GLProgram(shaderSourceFiles, "volume");
            
            volumeShader.addUniform(GLDataType.VEC3, "uColor");
            volumeShader.addUniform(GLDataType.MAT4, "uModel");
            volumeShader.addUniform(GLDataType.MAT4, "uView");
            volumeShader.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("volume", volumeShader);
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
        setGameMode         = new TCSetGameMode();
        
        XJGE.addCommand("showBoundingVolumes", showBoundingVolumes);
        XJGE.addCommand("setGameMode", setGameMode);
        
        XJGE.addObserver(setGameMode);
        
        XJGE.setClearColor(Color.SILVER);
        XJGE.setScene(new SceneExplore("map_test.txt"));
        XJGE.start();
        
    }
    
    public static boolean showBoundingVolumes() {
        return showBoundingVolumes.getValue();
    }
    
}