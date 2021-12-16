package org.xjge.scenes;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.graphics.BufferType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Shader;

/**
 * Dec 15, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", null);
        
        { //Establish game shaders
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("gameVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("gameFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            GLProgram gameProgram = new GLProgram(shaderSourceFiles, "game");
            gameProgram.use();
            
            gameProgram.addUniform(BufferType.INT,  "uType");
            gameProgram.addUniform(BufferType.FLOAT, "uBrightness");
            gameProgram.addUniform(BufferType.FLOAT, "uContrast");
            gameProgram.addUniform(BufferType.VEC3, "uColor");
            gameProgram.addUniform(BufferType.VEC3, "uAmbientColor");
            gameProgram.addUniform(BufferType.VEC3, "uDiffuseColor");
            gameProgram.addUniform(BufferType.VEC3, "uLightPosition");
            gameProgram.addUniform(BufferType.MAT3, "uNormal");
            gameProgram.addUniform(BufferType.MAT4, "uModel");
            gameProgram.addUniform(BufferType.MAT4, "uView");
            gameProgram.addUniform(BufferType.MAT4, "uProjection");
            
            XJGE.addGLProgram("game", gameProgram);
        }
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}