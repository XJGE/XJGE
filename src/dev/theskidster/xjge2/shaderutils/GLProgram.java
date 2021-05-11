package dev.theskidster.xjge2.shaderutils;

import dev.theskidster.xjge2.core.Logger;
import static dev.theskidster.xjge2.shaderutils.BufferType.*;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.LinkedList;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 2, 2021
 */

public class GLProgram {

    final int handle;
    
    public final String name;
    
    private final HashMap<String, Uniform> uniforms = new HashMap<>();
    private static final HashMap<BufferType, Integer> bufferSizes;
    
    static {
        bufferSizes = new HashMap<>() {{
            put(VEC2, 2);
            put(VEC3, 3);
            put(VEC4, 4);
            put(MAT2, 8);
            put(MAT3, 12);
            put(MAT4, 16);
        }};
    }
    
    public GLProgram(LinkedList<Shader> shaders, String name) {
        this.name = name;
        
        handle = glCreateProgram();
        shaders.forEach(shader -> glAttachShader(handle, shader.handle));
        glLinkProgram(handle);
        
        if(glGetProgrami(handle, GL_LINK_STATUS) != GL_TRUE) {
            Logger.setDomain("shaderutils");
            Logger.logSevere("Failed to link shader program \"" + name + "\". " + glGetProgramInfoLog(handle), null);
        } else {
            if(!name.equals("default")) {
                Logger.setDomain("shaderutils");
                Logger.logInfo("Shader program \"" + name + "\" linked successfully.");
                Logger.setDomain(null);
            }
        }
    }
    
    private Uniform createUniform(String name, Buffer buffer) {
        return new Uniform(glGetUniformLocation(handle, name), buffer);
    }
    
    public void addUniform(BufferType type, String name) {
        if(glGetUniformLocation(handle, name) == -1) {
            Logger.setDomain("shaderutils");
            Logger.logSevere("Failed to find uniform \"" + name + "\". Check " + 
                             "variable name or GLSL source file where it is declared.", 
                             null);
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            switch(type) {
                case INT              -> uniforms.put(name, createUniform(name, stack.mallocInt(1)));
                case FLOAT            -> uniforms.put(name, createUniform(name, stack.mallocFloat(1)));
                case VEC2, VEC3, VEC4 -> uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type))));
                case MAT2, MAT3, MAT4 -> uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type))));
            }
        }
    }
    
    public void use() {
        glUseProgram(handle);
    }
    
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name).location, value);
    }
    
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name).location, value);
    }
    
    public void setUniform(String name, Vector2f value) {
        glUniform2fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    public void setUniform(String name, Vector3f value) {
        glUniform3fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    public void setUniform(String name, Vector4f value) {
        glUniform4fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    public void setUniform(String name, boolean transpose, Matrix2f value) {
        glUniformMatrix2fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    public void setUniform(String name, boolean transpose, Matrix3f value) {
        glUniformMatrix3fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    public void setUniform(String name, boolean transpose, Matrix4f value) {
        glUniformMatrix4fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
}