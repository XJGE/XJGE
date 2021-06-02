package dev.theskidster.xjge2.graphics;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

public final class Color {

    //Functional
    public static final Color WHITE     = new Color(1);
    public static final Color SILVER    = new Color(0.75f);
    public static final Color GRAY      = new Color(0.5f);
    public static final Color DARK_GRAY = new Color(0.25f);
    public static final Color BLACK     = new Color();
    public static final Color RED       = new Color(255, 0, 0);
    public static final Color ORANGE    = new Color(255, 128, 0);
    public static final Color YELLOW    = new Color(255, 255, 0);
    public static final Color LIME      = new Color(0, 255, 0);
    public static final Color GREEN     = new Color(0, 128, 0);
    public static final Color BLUE      = new Color(0, 0, 255);
    public static final Color NAVY      = new Color(0, 0, 128);
    public static final Color PINK      = new Color(255, 0, 255);
    public static final Color PURPLE    = new Color(128, 0, 128);
    public static final Color CYAN      = new Color(0, 255, 255);
    public static final Color TEAL      = new Color(0, 128, 128);
    public static final Color BROWN     = new Color(64, 32, 0);
    
    //Fancy
    public static final Color RETRO_BLUE = new Color(92, 148, 252);
    public static final Color VIOLET     = new Color(141, 97, 156);
    public static final Color MIDNIGHT   = new Color(16, 21, 36);
    
    public final float r;
    public final float g;
    public final float b;
    
    private final Vector3f conversion;
    
    private Color() {
        r = g = b = 0;
        conversion = new Vector3f();
    }
    
    private Color(float scalar) {
        r = g = b = scalar;
        conversion = new Vector3f(scalar);
    }
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
        
        conversion = new Vector3f(this.r, this.g, this.b);
    }
    
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
    public Vector3f asVec3() {
        return conversion;
    }
    
    public boolean equals(Color other) {
        if(this == other) {
            return true;
        } else {
            return this.r == other.r && 
                    this.g == other.g && 
                    this.b == other.b;
        }
    }
    
}