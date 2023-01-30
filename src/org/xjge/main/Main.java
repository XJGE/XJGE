package org.xjge.main;

//Created: Jan 28, 2023

import java.util.LinkedList;
import org.joml.Vector2i;
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
        
        XJGE.init("/org/xjge/assets/", "org.xjge.main.", new Vector2i(384, 216));
        
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("palVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("palFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram palProgram = new GLProgram(shaderSourceFiles, "default");
            
            palProgram.use();
            palProgram.addUniform(BufferType.INT,  "uTexture");
            palProgram.addUniform(BufferType.MAT4, "uModel");
            palProgram.addUniform(BufferType.MAT4, "uView");
            palProgram.addUniform(BufferType.MAT4, "uProjection");
            
            for(int i = 0; i < 6; i++) {
                palProgram.addUniform(BufferType.VEC3, "uPalette[" + i + "]");
            }
            
            XJGE.addGLProgram("palShader", palProgram);
        }
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}
