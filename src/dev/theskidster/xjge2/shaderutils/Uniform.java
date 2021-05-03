package dev.theskidster.xjge2.shaderutils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author J Hoffman
 * Created: May 2, 2021
 */

final class Uniform {

    final int location;
    private final Buffer buffer;
    
    Uniform(int location, Buffer buffer) {
        this.location = location;
        this.buffer   = buffer;
    }
    
    IntBuffer asIntBuffer() {
        return (IntBuffer) buffer;
    }
    
    FloatBuffer asFloatBuffer() {
        return (FloatBuffer) buffer;
    }
    
}