package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;

//Created: May 2, 2021

/**
 * Encapsulates an OpenGL shader object. These objects contain the compiled 
 * results of a .glsl source file and provide it as a single stage of a much 
 * larger {@link GLProgram}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Shader {
    
    final int handle;
    
    /**
     * Parses a .glsl source file then utilizes the stage specified to produce 
     * a new OpenGL shader object.
     * 
     * @param filename the name of the .glsl file to parse. Expects the file 
     *                 extension to be included.
     * @param stage    the stage of the shader process this object will 
     *                 describe. One of:
     * <table>
     * <tr>
     * <td>{@link org.lwjgl.opengl.GL30#GL_VERTEX_SHADER GL_VERTEX_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER}</td>
     * </tr><tr>
     * <td>{@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER GL_TESS_CONTROL_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER GL_TESS_EVALUATION_SHADER}</td>
     * <td>{@link org.lwjgl.opengl.GL43#GL_COMPUTE_SHADER GL_COMPUTE_SHADER}</td>
     * </tr>
     * </table>
     */
    public Shader(String filename, int stage) {
        StringBuilder output = new StringBuilder();
        InputStream file     = Shader.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"))) {
            String line;
            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch(Exception e) {
            Logger.setDomain("shaderutils");
            Logger.logSevere("Failed to parse GLSL file \"" + filename + "\". " + 
                             "Check file name, path, or extension.", 
                             e);
        }
        
        CharSequence sourceCode = output.toString();
        
        handle = glCreateShader(stage);
        glShaderSource(handle, sourceCode);
        glCompileShader(handle);
        
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.setDomain("shader-utils");
            Logger.logSevere("Failed to parse GLSL file \"" + filename + "\". " + glGetShaderInfoLog(handle), null);
        }
    }
    
}