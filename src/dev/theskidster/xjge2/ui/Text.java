package dev.theskidster.xjge2.ui;

/**
 * @author J Hoffman
 * Created: May 18, 2021
 */

public class Text {

    /*
    private String prevText;
    private final Vector3f prevPosition;
    private Color prevColor;
    protected final Font font;
    protected Glyph prevGlyph;
    
    protected final HashMap<Integer, Glyph> glyphs = new HashMap<>();
    
    public Text(Font font) {
        this.font = font;
        
        prevText     = "";
        prevPosition = new Vector3f();
        prevColor    = Color.WHITE;
    }
    
    protected boolean textChanged(String text) {
        return !prevText.equals(text);
    }
    
    protected boolean positionChanged(Vector3f position) {
        return !prevPosition.equals(position);
    }
    
    protected boolean colorChanged(Color color) {
        return !prevColor.equals(color);
    }
    
    protected void updateChangeValues(String text, Vector3f position, Color color) {
        prevText = text;
        prevPosition.set(position);
        prevColor = color;
    }
    
    public void drawString(String text, Vector3f position, Color color) {
        boolean changed = textChanged(text) || positionChanged(position) || colorChanged(color);
        
        if(changed) {
            glyphs.clear();
            
            int advance = 0;
            int descent = 0;
            
            for(int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                
                if(c != '\n') {
                    Vector3f pos = new Vector3f(position.x + advance + font.getGlyphBearingLeft(c), 
                                                position.y + descent + font.getGlyphDescent(c),
                                                position.z);
                    glyphs.put(i, new Glyph(c, pos, color));
                    advance += font.getGlyphAdvance(c);
                } else {
                    advance = 0;
                    descent -= font.getSize();
                }
            }
        }
        
        font.draw(glyphs, changed);
        
        updateChangeValues(text, position, color);
    }
    
    public void drawString2(String text, Vector3f position, Color color) {
        int advance = 0;
        int descent = 0;

        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if(c != '\n') {
                Vector3f pos = new Vector3f(position.x + advance + font.getGlyphBearingLeft(c), 
                                            position.y + descent + font.getGlyphDescent(c),
                                            position.z);
                
                Glyph glyph = new Glyph(c, pos, color);
                
                boolean addGlyph = false;
                
                if(glyphs.size() > 0) {
                    for(int g = 0; g < glyphs.size(); g++) {
                        if(!glyph.equals(glyphs.get(g))) {
                            addGlyph = true;
                            
                            //System.out.println(glyphs.containsValue(prevGlyph));
                            
                            //System.out.println(glyphs.get(g).c + " " + c + ": " + g);
                            break;
                        }
                    }
                } else {
                    addGlyph = true;
                }
                
                if(addGlyph) {
                    prevGlyph = glyph;
                    glyphs.put(glyphs.size(), glyph);
                }

                advance += font.getGlyphAdvance(c);
            } else {
                advance = 0;
                descent -= font.getSize();
            }
        }
        
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
    }*/
    
}