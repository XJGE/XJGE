package org.xjge.graphics;

import org.xjge.core.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;

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
    public GLShader(String filepath, String filename, int stage) {
        StringBuilder output = new StringBuilder();
        InputStream file     = GLShader.class.getResourceAsStream(filepath + filename);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"))) {
            String line;
            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch(Exception e) {
            Logger.logError("Failed to parse GLSL file \"" + filename + "\". " + 
                             "Check filename, path, or extension", 
                             e);
        }
        
        CharSequence sourceCode = output.toString();
        
        handle = glCreateShader(stage);
        glShaderSource(handle, sourceCode);
        glCompileShader(handle);
        
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.logError("Failed to parse GLSL file \"" + filename + "\" (" + glGetShaderInfoLog(handle) + ")", null);
        }
    }
    
}