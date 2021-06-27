package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: May 2, 2021
 */

public final class Shader {
    
    final int handle;
    
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