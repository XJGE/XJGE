package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

public abstract class Widget {

    private int drawOrder;
    protected int width;
    protected int height;
    
    public Vector3i position = new Vector3i();
    
    private final HashMap<Integer, FontVertexData> fontData = new HashMap<>();
    private final HashMap<Integer, Font2> prevFont          = new HashMap<>();
    private final HashMap<Integer, String> prevText         = new HashMap<>();
    private final HashMap<Integer, Vector3f> prevPosition   = new HashMap<>();
    private final HashMap<Integer, Color>  prevColor        = new HashMap<>();
    
    private final HashMap<Integer, HashMap<Integer, Glyph>> glyphs = new HashMap<>();
    
    public Widget(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    public abstract void update(); //TODO: cursorX/Y?
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void setSplitPosition();
    
    int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
    private <T> boolean valueChanged(HashMap<Integer, T> prevValue, T currValue) {
        if(prevValue.containsKey(drawOrder)) {
            return !prevValue.get(drawOrder).equals(currValue);
        } else {
            return true;
        }
    }
    
    private <T> void updateValue(HashMap<Integer, T> prevValue, T currValue) {
        if(valueChanged(prevValue, currValue)) prevValue.put(drawOrder, currValue);
    }
    
    protected void drawString(Font2 font, String text, Vector3f position, Color color) {
        drawOrder++;
        
        if(!prevPosition.containsKey(drawOrder)) prevPosition.put(drawOrder, new Vector3f());
        
        boolean changed = valueChanged(prevFont, font) || valueChanged(prevText, text) || 
                           valueChanged(prevPosition, position) || valueChanged(prevColor, color);
        
        if(changed) {
            if(glyphs.containsKey(drawOrder)) glyphs.get(drawOrder).clear();
            else                              glyphs.put(drawOrder, new HashMap<>());
            
            if(valueChanged(prevFont, font)) {
                fontData.put(drawOrder, new FontVertexData(font));
            }
            
            int advance = 0;
            int descent = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                
                if(c != '\n') {
                    Vector3f pos = new Vector3f(position.x + advance + font.getGlyphBearing(c), 
                                                position.y + descent + font.getGlyphDescent(c),
                                                position.z);
                    glyphs.get(drawOrder).put(i, new Glyph(c, pos, color));
                    advance += font.getGlyphAdvance(c);
                } else {
                    advance = 0;
                    descent -= font.size;
                }
            }
        }
        
        fontData.get(drawOrder).render(font, glyphs.get(drawOrder), changed);
        
        updateValue(prevFont, font);
        updateValue(prevText, text);
        updateValue(prevPosition, prevPosition.get(drawOrder).set(position));
        updateValue(prevColor, color);
    }
    
    void resetStringIndex() {
        drawOrder = 0;
    }
    
}