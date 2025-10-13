package org.xjge.test;

import java.util.HashMap;
import java.util.LinkedList;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.Control;
import static org.xjge.core.Control.CIRCLE;
import static org.xjge.core.Control.CROSS;
import static org.xjge.core.Control.DPAD_DOWN;
import static org.xjge.core.Control.DPAD_LEFT;
import static org.xjge.core.Control.DPAD_RIGHT;
import static org.xjge.core.Control.DPAD_UP;
import static org.xjge.core.Control.L1;
import static org.xjge.core.Control.L2;
import static org.xjge.core.Control.L3;
import static org.xjge.core.Control.LEFT_STICK_X;
import static org.xjge.core.Control.LEFT_STICK_Y;
import static org.xjge.core.Control.OPTIONS;
import static org.xjge.core.Control.PS_BUTTON;
import static org.xjge.core.Control.R1;
import static org.xjge.core.Control.R2;
import static org.xjge.core.Control.R3;
import static org.xjge.core.Control.RIGHT_STICK_X;
import static org.xjge.core.Control.RIGHT_STICK_Y;
import static org.xjge.core.Control.SHARE;
import static org.xjge.core.Control.SQUARE;
import static org.xjge.core.Control.TRIANGLE;
import org.xjge.core.Input;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLDataType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;

/**I
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
        
        {
            var shaderSourceFiles = new LinkedList<GLShader>() {{
                add(new GLShader("shader_mudball_vertex.glsl", GL_VERTEX_SHADER));
                add(new GLShader("shader_mudball_fragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram mudballShader = new GLProgram(shaderSourceFiles, "mudball");
            
            mudballShader.addUniform(GLDataType.INT,  "uTexture");
            mudballShader.addUniform(GLDataType.VEC3, "uColor");
            mudballShader.addUniform(GLDataType.MAT4, "uModel");
            mudballShader.addUniform(GLDataType.MAT4, "uView");
            mudballShader.addUniform(GLDataType.MAT4, "uProjection");
            
            XJGE.addGLProgram("mudball", mudballShader);
        }
        
        Window.addObserver(new WindowObserver());
        
        //Window.setMinimumSize(640, 480);
        //Window.setSize(640, 480);
        //Window.setTitle("Test");
        //Window.setIcon("icon.png");
        //Window.setResolution(384, 216);
        Window.setResizable(false);
        //Window.setFullscreen(true);
        Window.focus();
        
        showBoundingVolumes = new TCShowBoundingVolumes();
        setGameMode         = new TCSetGameMode();
        
        XJGE.addCommand("showBoundingVolumes", showBoundingVolumes);
        XJGE.addCommand("setGameMode", setGameMode);
        
        XJGE.addObserver(setGameMode);
        
        var controlBindings = new HashMap<Control, Integer>(){{}};
        controlBindings.put(CIRCLE, GLFW_KEY_Q);
        Input.setDeviceControlBindings(Input.KEYBOARD, controlBindings);
        
        XJGE.setClearColor(Color.SILVER);
        XJGE.setScene(new Scene3D("map_test.txt"));
        XJGE.start();
        
    }
    
    public static boolean showBoundingVolumes() {
        return showBoundingVolumes.getValue();
    }
    
}