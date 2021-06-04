package dev.theskidster.xjge2.core;

import java.util.ArrayList;

/**
 * @author J Hoffman
 * Created: Jun 4, 2021
 */

public final class Text {

    private Text() {}
    
    public static String wrap(String text, int advanceLimit, Font2 font) {
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
    
    public static int lengthInPixels(String text, Font2 font) {
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