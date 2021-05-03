package dev.theskidster.xjge2.shaderutils;

/**
 * @author J Hoffman
 * Created: May 2, 2021
 */

/**
 * Represents a GLSL data type that will be used to allocate a buffer of corresponding size.
 */
public enum BufferType {
    /**
     * A signed, two's complement, 32-bit integer.
     */
    INT,
    
    /**
     * An IEEE-754 single-precision floating point number.
     */
    FLOAT,
    
    /**
     * A vector comprised of two single-precision floating-point numbers.
     */
    VEC2, 
    
    /**
     * A vector comprised of three single-precision floating-point numbers.
     */
    VEC3, 
    
    /**
     * A vector comprised of four single-precision floating-point numbers.
     */
    VEC4,
    
    /**
     * A matrix comprised of 2x2 single-precision floating-point numbers.
     */
    MAT2, 
    
    /**
     * A matrix comprised of 3x3 single-precision floating-point numbers.
     */
    MAT3, 
    
    /**
     * A matrix comprised of 4x4 single-precision floating-point numbers.
     */
    MAT4;
}