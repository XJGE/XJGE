package org.xjge.graphics;

import org.xjge.core.Logger;
import static org.xjge.graphics.BufferType.*;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

//Created: May 2, 2021

/**
 * Represents a completed shader program comprised of multiple {@link Shader} 
 * objects that specify how data sent to the GPU will be processed at different 
 * rendering stages while the program is active.
 * 
 * @author J Hoffman
 * @since  2.0.0
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
    
    /**
     * Creates a new shader program with the code supplied from the compiled 
     * .glsl source files.
     * 
     * @param shaders the objects representing various stages of the rendering 
     *                pipeline
     * @param name    the name used to identify the program should it fail to 
     *                link properly
     */
    public GLProgram(LinkedList<Shader> shaders, String name) {
        this.name = name;
        
        handle = glCreateProgram();
        shaders.forEach(shader -> glAttachShader(handle, shader.handle));
        glLinkProgram(handle);
        
        if(glGetProgrami(handle, GL_LINK_STATUS) != GL_TRUE) {
            Logger.setDomain("graphics");
            Logger.logSevere("Failed to link shader program \"" + name + "\" (" + glGetProgramInfoLog(handle) + ")", null);
        } else {
            if(!name.equals("default")) {
                Logger.setDomain("graphics");
                Logger.logInfo("Shader program \"" + name + "\" linked successfully");
                Logger.setDomain(null);
            }
        }
    }
    
    /**
     * Generates a new {@link Uniform} object.
     * 
     * @param name   the unique name used to identify the uniform variable as 
     *               it appears in the .glsl source file
     * @param buffer the data buffer that stores the value which will be 
     *               supplied to the GPU at runtime
     * 
     * @return a new uniform object
     */
    private Uniform createUniform(String name, Buffer buffer) {
        return new Uniform(glGetUniformLocation(handle, name), buffer);
    }
    
    /**
     * Creates an association between a CPU-stored data buffer holding the 
     * value of a {@linkplain Uniform uniform variable} and its corresponding 
     * memory location on the GPU.
     * <p>
     * More specifically, this method allocates a new data buffer on the CPU 
     * with a size corresponding to the GLSL data type specified, then locates 
     * the memory address of the buffer on the GPU that holds the value of the 
     * uniform variable and provides that information in an object which will 
     * wrap this state for the engine to use during rendering operations. 
     * 
     * @param type the GLSL data type of the uniform variable
     * @param name the unique name used to identify the uniform variable as it 
     *             appears in the .glsl source file
     */
    public void addUniform(BufferType type, String name) {
        if(glGetUniformLocation(handle, name) == -1) {
            Logger.setDomain("graphics");
            Logger.logSevere("Failed to find uniform variable \"" + name + "\". Check " + 
                             "variable name or GLSL source file where it is declared", 
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
    
    /**
     * Sets this as the current shader program the GPU will use for all 
     * subsequent rendering operations.
     */
    public void use() {
        glUseProgram(handle);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name).location, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name).location, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector2f value) {
        glUniform2fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector3f value) {
        glUniform3fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector4f value) {
        glUniform4fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable 
     *                  as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value 
     *                  parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix2f value) {
        glUniformMatrix2fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable 
     *                  as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value 
     *                  parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix3f value) {
        glUniformMatrix3fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable 
     *                  as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value 
     *                  parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix4f value) {
        glUniformMatrix4fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new values. This variant 
     * of {@code setUniform} is modified so multiple values can be sent under a 
     * single name- specifically for instances where the uniform variable in 
     * question is actually a collection such as an array.
     * 
     * @param name      the unique name used to identify the uniform variable 
     *                  array as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value 
     *                  parameter will be transposed before it is read
     * @param values    a collection of values of the uniform variable array
     */
    public void setUniform(String name, boolean transpose, List<Matrix4f> values) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matBuf = stack.mallocFloat(16 * values.size() - 1);
            
            for(int i = 0; i < values.size() - 1; i++) values.get(i).get(16 * i, matBuf);
            
            glUniformMatrix4fv(
                    uniforms.get(name).location,
                    transpose,
                    matBuf);
        }
    }
    
    /**
     * Checks whether this shader program contains a uniform variable at any 
     * stage in its pipeline.
     * 
     * @param name the unique name used to identify the uniform variable as it 
     *              appears in the .glsl source file
     * 
     * @return true if this program contains a uniform variable by the name
     *          specified
     */
    public boolean containsUniform(String name) {
        return uniforms.containsKey(name);
    }
    
}