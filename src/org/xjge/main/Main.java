package org.xjge.main;

//Created: Jan 25, 2023

import java.util.LinkedList;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.graphics.BufferType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Shader;


/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.test.", new Vector2i(16, 9));
        Game.setScene(new TestScene());
        
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("palVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("palFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            PaletteProcess paletteShader = new PaletteProcess(new GLProgram(shaderSourceFiles, "palette"));
                
            paletteShader.glProgram.use();
            paletteShader.glProgram.addUniform(BufferType.INT,  "uTexture");
            paletteShader.glProgram.addUniform(BufferType.MAT4, "uProjection");
            
            XJGE.usePostProcessShader(GLFW_JOYSTICK_1, paletteShader);
        }
        
        XJGE.start();
        
    }
    
}
