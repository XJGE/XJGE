package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jun 4, 2021
 */

public final class Text {

    private int drawOrder;
    
    private final int PADDING = 4;
    
    private final HashMap<Integer, FontVertexData> fontData = new HashMap<>();
    private final HashMap<Integer, Font> prevFont           = new HashMap<>();
    private final HashMap<Integer, String> prevText         = new HashMap<>();
    private final HashMap<Integer, Vector2i> prevPosition   = new HashMap<>();
    private final HashMap<Integer, Color>  prevColor        = new HashMap<>();
    
    private final HashMap<Integer, HashMap<Integer, Glyph>> glyphs = new HashMap<>();
    
    Text() {}
    
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
    
    void drawString(Font font, String text, Vector2i position, Color color) {
        drawOrder++;
        
        if(!prevPosition.containsKey(drawOrder)) prevPosition.put(drawOrder, new Vector2i());
        
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
                    Vector2i pos = new Vector2i(position.x + advance + font.getGlyphBearing(c), 
                                                position.y + descent + font.getGlyphDescent(c));
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
    
    void drawCommand(Font font, String text, Vector2i position) {
        drawOrder++;
        
        if(!prevPosition.containsKey(drawOrder)) prevPosition.put(drawOrder, new Vector2i());
        if(!fontData.containsKey(drawOrder)) fontData.put(drawOrder, new FontVertexData(font));
        
        boolean changed = valueChanged(prevText, text) || valueChanged(prevPosition, position);
        
        if(changed) {
            if(glyphs.containsKey(drawOrder)) glyphs.get(drawOrder).clear();
            else                              glyphs.put(drawOrder, new HashMap<>());
            
            int advance = 0;
            int start   = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if(c == ' ') start = i;
                
                Vector2i pos = new Vector2i(position.x + advance + font.getGlyphBearing(c),
                                            position.y + font.getGlyphDescent(c));
                
                Color col = (start != 0 && i > start) ? Color.YELLOW : Color.CYAN;
                
                switch(c) {
                    case '(', ')', ',', '<', '>' -> col = Color.WHITE;
                }
                
                glyphs.get(drawOrder).put(i, new Glyph(c, pos, col));
                advance += font.getGlyphAdvance(c);
            }
        }
        
        fontData.get(drawOrder).render(font, glyphs.get(drawOrder), changed);
        
        updateValue(prevText, text);
        updateValue(prevPosition, prevPosition.get(drawOrder).set(position));
    }
    
    void drawOutput(Font font, TerminalOutput[] o1, TerminalOutput o2, int index, boolean executed, Rectangle rectangle) {
        drawOrder++;
        
        if(!prevPosition.containsKey(drawOrder)) prevPosition.put(drawOrder, new Vector2i());
        if(!fontData.containsKey(drawOrder)) fontData.put(drawOrder, new FontVertexData(font));
        
        if(executed) {
            glyphs.get(drawOrder).clear();
            
            int advance = 0;
            int descent = 0;
            int yOffset = (font.size + PADDING) * Text.numCharOccurences(o2.text, '\n', 0);
            
            if(index != 0) {
                String composite = "";
                for(int i = 0; i < index; i++) composite += o1[i].text;
                yOffset += (font.size + PADDING) * Text.numCharOccurences(composite, '\n', 0);
            }
            
            for(int i = 0; i < o2.text.length(); i++) {
                char c = o2.text.charAt(i);
                
                if(c != '\n') {
                    Vector2i pos = new Vector2i(advance + font.getGlyphBearing(c), 
                                               (yOffset + descent + font.getGlyphDescent(c) + font.size / 4));
                    
                    glyphs.get(drawOrder).put(i, new Glyph(c, pos, o2.color));
                    advance += font.getGlyphAdvance(c);
                } else {
                    advance  = 0;
                    descent -= font.size + PADDING;
                }
            }
            
            rectangle.xPos   = 0;
            rectangle.yPos   = yOffset + descent + font.size + PADDING;
            rectangle.width  = Window.getWidth();
            rectangle.height = Math.abs(descent);
        }
        
        fontData.get(drawOrder).render(font, glyphs.get(drawOrder), executed);
    }
    
    void resetStringIndex() {
        drawOrder = 0;
    }
    
    public static String wrap(String text, int advanceLimit, Font font) {
        var words        = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if(i != text.length() - 1) {
                if(c != ' ') {
                    sb.append(c);
                } else {
                    words.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                sb.append(c);
                words.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        
        int wordLength = 0;
        text = "";

        for(int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            wordLength += lengthInPixels(word + " ", font);
            
            if(i != words.size() - 1 && wordLength + lengthInPixels(words.get(i + 1), font) > advanceLimit) {
                text += words.get(words.indexOf(word)).concat("\n");
                wordLength = 0;
            } else {
                if(words.indexOf(word) != words.size() - 1) {
                    text += words.get(words.indexOf(word)).concat(" ");
                } else {
                    text += words.get(words.indexOf(word));
                }
            }
        }
        
        return text;
    }
    
    public static int lengthInPixels(String text, Font font) {
        int length = 0;
        
        for(char c : text.toCharArray()) {
            if(c != '\n') {
                length += font.getGlyphAdvance(c);
            }
        }
        
        return length;
    }
    
    public static int numCharOccurences(String text, char c, int index) {
        if(index >= text.length()) return 0;
        int count = (text.charAt(index) == c) ? 1 : 0;
        
        return count + numCharOccurences(text, c, index + 1);
    }
    
}