package org.xjge.graphics;

import org.xjge.core.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.lwjgl.opengl.GL20.*;
import org.xjge.core.AssetManager;

/**
 * Created: May 2, 2021
 * <br><br>
 * Encapsulates an OpenGL shader object. These objects contain the compiled 
 * results of a .glsl source file and provide it as a single stage of a much 
 * larger {@linkplain GLProgram shader program}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class GLShader {
    
    final int handle;
    
    /**
     * Parses a .glsl source file then utilizes the stage specified to produce 
     * a new OpenGL shader object.
     * 
     * @param filename the name of the .glsl file to parse. Expects the file 
     *                 extension to be included.
     * @param stage the stage of the shader process this object will describe. One of:
     * <table><caption></caption><tr>
     * <td>{@link org.lwjgl.opengl.GL30#GL_VERTEX_SHADER GL_VERTEX_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER}</td>
     * </tr><tr>
     * <td>{@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER GL_TESS_CONTROL_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER GL_TESS_EVALUATION_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL43#GL_COMPUTE_SHADER GL_COMPUTE_SHADER}</td>
     * </tr></table>
     */
    public GLShader(String filename, int stage) {
        String sourceCode = "";
        
        try(InputStream file = AssetManager.open(filename)) {
            sourceCode = loadShader(file);
        } catch(Exception exception) {
            Logger.logError("Failed to load GLSL file \"" + filename + "\"", exception);
        }
        
        handle = glCreateShader(stage);
        glShaderSource(handle, sourceCode);
        glCompileShader(handle);
        
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.logError("Failed to parse GLSL file \"" + filename + "\" (" + glGetShaderInfoLog(handle) + ")", null);
        }
    }
    
    private static String loadShader(InputStream file) throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }

            return builder.toString();
        }
    }
    
}