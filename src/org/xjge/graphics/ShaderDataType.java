package org.xjge.graphics;

import static org.lwjgl.opengl.GL20.*;

/**
 * Created: May 2, 2021
 * <br><br>
 * Represents a GLSL data type. These are used to determine how much data a 
 * single buffer should contain.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public enum ShaderDataType {
    
    INT(GL_INT),
    FLOAT(GL_FLOAT),
    VEC2(GL_FLOAT_VEC2),
    VEC3(GL_FLOAT_VEC3),
    VEC4(GL_FLOAT_VEC4),
    MAT2(GL_FLOAT_MAT2),
    MAT3(GL_FLOAT_MAT3),
    MAT4(GL_FLOAT_MAT4);

    public final int glType;
    ShaderDataType(int glType) { this.glType = glType; }

    public static ShaderDataType fromGLConstant(int glType) {
        for(ShaderDataType type : values()) {
            if (type.glType == glType) return type;
        }
        return null;
    }
    
}