package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.ui.Font;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

public abstract class Widget {

    protected int stringIndex;
    
    protected int width;
    protected int height;
    
    public Vector3i position = new Vector3i();
    
    public Widget(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    public abstract void update(); //TODO: cursorX/Y?
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void setSplitPosition();
    
    public int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
    protected void drawString(Font font, String text, Vector3f position, Color color) {
        /*
        I've decided it would be best to move the functionality of the Text 
        class into this one and reduce the need for extra objects altogether.
        
        I suspect there's a performance bottleneck pertaining to the current
        implementations failure to tightly control calls to glBufferData(). As
        such, all subsequent calls to drawString() will be assigned index 
        numbers denoting to the order in which they were called. This number 
        will be used to organize various collections containing the previous 
        values of the methods parameters as they were in the prior frame.
        
        Using this saved state we can better determine whether a memory 
        allocation will need to be performed upon the change of one or more of 
        these values.
        
        
        
        int stringIndex;
        
        TreeMap<Integer, Font>     prevFont;
        TreeMap<Integer, String>   prevText;
        TreeMap<Integer, Vector3f> prevPos;
        TreeMap<Integer, Color>    prevCol;
        
        */
        
        stringIndex++;
    }
    
    protected void setFont(Font font) {
        //maybe just include in drawString()?
        stringIndex = 0;
    }
    
    void resetStringIndex() {
        stringIndex = 0;
    }
    
}