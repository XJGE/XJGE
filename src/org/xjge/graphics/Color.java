package org.xjge.graphics;

import java.util.Random;
import org.joml.Vector3f;
import org.xjge.core.XJGE;

/**
 * An object used to represent color.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Color {
    
    public static final Color WHITE   = new Color(true, 255, 255, 255);
    public static final Color SILVER  = new Color(true, 192, 192, 192);
    public static final Color GRAY    = new Color(true, 128, 128, 128);
    public static final Color BLACK   = new Color(true,   0,   0,   0);
    public static final Color RED     = new Color(true, 255,  64,  64);
    public static final Color BROWN   = new Color(true, 144,  80,   0);
    public static final Color YELLOW  = new Color(true, 255, 255,  80);
    public static final Color ORANGE  = new Color(true, 255, 176,   0);
    public static final Color GREEN   = new Color(true,  64, 255,  64);
    public static final Color EMERALD = new Color(true,   0, 176,   0);
    public static final Color CYAN    = new Color(true, 128, 255, 255);
    public static final Color TEAL    = new Color(true,   0, 144, 144);
    public static final Color BLUE    = new Color(true,  64,  64, 255);
    public static final Color NAVY    = new Color(true,   0,   0, 176);
    public static final Color MAGENTA = new Color(true, 255,  80, 255);
    public static final Color PURPLE  = new Color(true, 144,  48, 176);
    
    private final boolean immutable;
    
    private final Vector3f components;
    
    private static final Random random = new Random();
    
    private Color(boolean immutable, float red, float green, float blue) {
        this.immutable = immutable;
        components = new Vector3f(XJGE.clampValue(0f, 1f, red), XJGE.clampValue(0f, 1f, green), XJGE.clampValue(0f, 1f, blue));
    }
    
    private Color(boolean immutable, int red, int green, int blue) {
        this(immutable, XJGE.clampValue(0, 255, red) / 255f, 
                        XJGE.clampValue(0, 255, green) / 255f, 
                        XJGE.clampValue(0, 255, blue) / 255f);
    }
    
    public Color(float red, float green, float blue) {
        this(false, red, green, blue);
    }
    
    /**
     * Creates a new color object using the values of the three RGB components 
     * supplied. Component values are expected to be within the range of 0 to 255.
     * 
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     */
    public Color(int red, int green, int blue) {
        this(false, XJGE.clampValue(0, 255, red) / 255f, XJGE.clampValue(0, 255, green) / 255f, XJGE.clampValue(0, 255, blue) / 255f);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Color other = (Color) obj;
        return Float.compare(getRed(), other.getRed()) == 0 &&
               Float.compare(getGreen(), other.getGreen()) == 0 &&
               Float.compare(getBlue(), other.getBlue()) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(getRed());
        result = 31 * result + Float.hashCode(getGreen());
        result = 31 * result + Float.hashCode(getBlue());
        return result;
    }
    
    public void copy(Color other) {
        if(immutable) return;
        components.set(other.asVector3f());
    }
    
    public void randomize() {
        if(immutable) return;
        components.set(random.nextInt(256) / 255f, random.nextInt(256) / 255f, random.nextInt(256) / 255f);
    }
    
    public void setRed(float value) {
        if(immutable) return;
        components.x = XJGE.clampValue(0f, 1f, value);
    }
    
    public void setGreen(float value) {
        if(immutable) return;
        components.y = XJGE.clampValue(0f, 1f, value);
    }
    
    public void setBlue(float value) {
        if(immutable) return;
        components.z = XJGE.clampValue(0f, 1f, value);
    }
    
    public void set(float red, float green, float blue) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }
    
    public void setRed(int value) {
        setRed(XJGE.clampValue(0, 255, value) / 255f);
    }
    
    public void setGreen(int value) {
        setGreen(XJGE.clampValue(0, 255, value) / 255f);
    }
    
    public void setBlue(int value) {
        setBlue(XJGE.clampValue(0, 255, value) / 255f);
    }
    
    public void set(int red, int green, int blue) {
        set(XJGE.clampValue(0, 255, red) / 255f, XJGE.clampValue(0, 255, green) / 255f, XJGE.clampValue(0, 255, blue) / 255f);
    }
    
    public float getRed() {
        return components.x;
    }
    
    public float getGreen() {
        return components.y;
    }
    
    public float getBlue() {
        return components.z;
    }
    
    /**
     * Returns the RGB component values of this object as a three-component 
     * floating point vector that can be passed to a {@linkplain GLProgram 
     * shader program} through a uniform variable.
     * 
     * @return the value of this color RGB components as a three-component 
     *         floating point vector struct
     */
    public final Vector3f asVector3f() {
        return components;
    }
    
}