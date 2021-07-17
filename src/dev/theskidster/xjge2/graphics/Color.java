package dev.theskidster.xjge2.graphics;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

/**
 * An immutable object used to represent color.
 */
public final class Color {
    
    public static final Color WHITE   = new Color(1);
    public static final Color SILVER  = new Color(0.753f);
    public static final Color GRAY    = new Color(0.38f);
    public static final Color BLACK   = new Color(0);
    public static final Color RED     = new Color(255, 0, 0);
    public static final Color ORANGE  = new Color(255, 153, 0);
    public static final Color YELLOW  = new Color(255, 255, 0);
    public static final Color LIME    = new Color(0, 255, 0);
    public static final Color GREEN   = new Color(0, 153, 0);
    public static final Color TEAL    = new Color(0, 153, 153);
    public static final Color CYAN    = new Color(0, 255, 255);
    public static final Color BLUE    = new Color(0, 0, 255);
    public static final Color PURPLE  = new Color(153, 51, 204);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color PINK    = new Color(255, 153, 204);
    public static final Color BROWN   = new Color(102, 51, 0);
    
    public final float r;
    public final float g;
    public final float b;
    
    private final Vector3f conversion;
    
    /**
     * Creates a new achromatic shade (gray) using the specified scalar value. 
     * 
     * @param scalar the value all RGB components will be set to (between 0 and 1)
     */
    private Color(float scalar) {
        r = g = b = scalar;
        conversion = new Vector3f(scalar);
    }
    
    /**
     * Creates a new color object using the values of the three RGB components supplied. Component values are expected to be within the
     * range of 0 to 255.
     * 
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     */
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
        
        conversion = new Vector3f(this.r, this.g, this.b);
    }
    
    /**
     * Creates a new color object using the values of the three RGB components supplied. Component values are expected to be within the
     * range of 0 to 255.
     * 
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     * 
     * @return a new immutable color object generated with the specified RGB components
     */
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
    /**
     * Creates a random color.
     * 
     * @return a new immutable color object that exhibits randomized color component values
     */
    public static Color random() {
        return new Color(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255));
    }
    
    /**
     * Returns the RGB component values of this object as a three-component floating point vector that can be passed to a 
     * {@linkplain GLProgram shader program} through a uniform variable.
     * 
     * @return the value of this color RGB components as a three-component floating point vector struct
     */
    public Vector3f asVec3() {
        return conversion;
    }
    
    /**
     * Compares the RGB component values of this color to another.
     * 
     * @param other the color to test this one against
     * 
     * @return true if the colors are the same
     */
    public boolean compareTo(Color other) {
        if(this == other) {
            return true;
        } else {
            return this.r == other.r && 
                    this.g == other.g && 
                    this.b == other.b;
        }
    }
    
}