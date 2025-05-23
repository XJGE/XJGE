package org.xjge.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created: May 2, 2021
 * <br><br>
 * Provides a type-agnostic data buffer which is used to store the value of a 
 * uniform variable. This value will be retrieved at a later point as the 
 * desired data type and supplied to the current {@linkplain GLProgram shader 
 * program}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class GLUniform {

    final int location;
    
    private final Buffer buffer;
    
    /**
     * Creates a structure that will hold the value of a uniform variable.
     * 
     * @param location the unique number supplied by OpenGL used to identify 
     *                 the uniform variable
     * @param buffer a type-neutral data buffer used to store the value of the 
     *               uniform variable
     */
    GLUniform(int location, Buffer buffer) {
        this.location = location;
        this.buffer   = buffer;
    }
    
    /**
     * Provides the data buffer holding the value of the uniform variable 
     * associated with this object.
     * 
     * @return the value of the uniform variable contained in an {@code IntBuffer}
     */
    IntBuffer asIntBuffer() {
        return (IntBuffer) buffer;
    }
    
    /**
     * Provides the data buffer holding the value of the uniform variable 
     * associated with this object.
     * 
     * @return the value of the uniform variable contained in a {@code FloatBuffer}
     */
    FloatBuffer asFloatBuffer() {
        return (FloatBuffer) buffer;
    }
    
}