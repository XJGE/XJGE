package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import static dev.theskidster.xjge2.core.Font.DEFAULT_SIZE;
import dev.theskidster.xjge2.graphics.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: May 23, 2021
 */

final class Terminal implements PropertyChangeListener {

    private int xIndex;
    private int yIndex;
    private static int shiftElements = -1;
    private final int glyphAdvance;
    
    private boolean cursorIdle;
    private boolean cursorBlink;
    private boolean suggest;
    private boolean executed = true;
    
    private final Vector2i caretPos   = new Vector2i(0, DEFAULT_SIZE / 4);
    private final Vector2i cursorPos  = new Vector2i(0, DEFAULT_SIZE / 4);
    private final Vector2i commandPos = new Vector2i(0, DEFAULT_SIZE / 4);
    
    private String suggestion = "";
    private String prevTyped  = "";
    
    private final Font font;
    private final Text text           = new Text();
    private final StringBuilder typed = new StringBuilder();
    private final Timer timer         = new Timer(1, 20, this);
    
    RectangleBatch rectBatch1;
    RectangleBatch rectBatch2;
    private static final LinkedHashMap<Integer, Rectangle> opaqueRectangles = new LinkedHashMap<>();
    
    TreeMap<String, TerminalCommand> commands;
    private final ArrayList<String> cmdHistory      = new ArrayList<>();
    private final TerminalOutput[] cmdOutput        = new TerminalOutput[5];
    private final HashMap<Integer, Integer> charPos = new HashMap<>();
    
    private static final HashMap<Integer, Key> keyChars;
    
    private static class Key {
        private final char c;
        private final char C;
        
        Key(char c, char C) {
            this.c = c;
            this.C = C;
        }
        
        char getChar(boolean shiftHeld) {
            return (!shiftHeld) ? c : C; 
        }
    }
    
    static {
        keyChars = new HashMap<>() {{
            put(GLFW_KEY_SPACE,      new Key(' ', ' '));
            put(GLFW_KEY_APOSTROPHE, new Key('\'', '\"'));
            put(GLFW_KEY_COMMA,      new Key(',', '<'));
            put(GLFW_KEY_MINUS,      new Key('-', '_'));
            put(GLFW_KEY_PERIOD,     new Key('.', '>'));
            put(GLFW_KEY_SLASH,      new Key('/', '?'));
            put(GLFW_KEY_0, new Key('0', ')'));
            put(GLFW_KEY_1, new Key('1', '!'));
            put(GLFW_KEY_2, new Key('2', '@'));
            put(GLFW_KEY_3, new Key('3', '#'));
            put(GLFW_KEY_4, new Key('4', '$'));
            put(GLFW_KEY_5, new Key('5', '%'));
            put(GLFW_KEY_6, new Key('6', '^'));
            put(GLFW_KEY_7, new Key('7', '&'));
            put(GLFW_KEY_8, new Key('8', '*'));
            put(GLFW_KEY_9, new Key('9', '('));
            put(GLFW_KEY_SEMICOLON, new Key(';', ':'));
            put(GLFW_KEY_EQUAL,     new Key('=', '+'));
            put(GLFW_KEY_A, new Key('a', 'A'));
            put(GLFW_KEY_B, new Key('b', 'B'));
            put(GLFW_KEY_C, new Key('c', 'C'));
            put(GLFW_KEY_D, new Key('d', 'D'));
            put(GLFW_KEY_E, new Key('e', 'E'));
            put(GLFW_KEY_F, new Key('f', 'F'));
            put(GLFW_KEY_G, new Key('g', 'G'));
            put(GLFW_KEY_H, new Key('h', 'H'));
            put(GLFW_KEY_I, new Key('i', 'I'));
            put(GLFW_KEY_J, new Key('j', 'J'));
            put(GLFW_KEY_K, new Key('k', 'K'));
            put(GLFW_KEY_L, new Key('l', 'L'));
            put(GLFW_KEY_M, new Key('m', 'M'));
            put(GLFW_KEY_N, new Key('n', 'N'));
            put(GLFW_KEY_O, new Key('o', 'O'));
            put(GLFW_KEY_P, new Key('p', 'P'));
            put(GLFW_KEY_Q, new Key('q', 'Q'));
            put(GLFW_KEY_R, new Key('r', 'R'));
            put(GLFW_KEY_S, new Key('s', 'S'));
            put(GLFW_KEY_T, new Key('t', 'T'));
            put(GLFW_KEY_U, new Key('u', 'U'));
            put(GLFW_KEY_V, new Key('v', 'V'));
            put(GLFW_KEY_W, new Key('w', 'W'));
            put(GLFW_KEY_X, new Key('x', 'X'));
            put(GLFW_KEY_Y, new Key('y', 'Y'));
            put(GLFW_KEY_Z, new Key('z', 'Z'));
            put(GLFW_KEY_LEFT_BRACKET,  new Key('[', '{'));
            put(GLFW_KEY_BACKSLASH,     new Key('\\', '|'));
            put(GLFW_KEY_RIGHT_BRACKET, new Key(']', '}'));
            put(GLFW_KEY_GRAVE_ACCENT,  new Key('`', '~'));
        }};
        
        for(int i = 0; i < 5; i++) opaqueRectangles.put(i, new Rectangle());
    }
    
    Terminal(TreeMap<String, TerminalCommand> commands, Font font) {
        this.commands = commands;
        this.font     = font;
        
        glyphAdvance = (int) font.getGlyphAdvance('>');
        cursorPos.x  = glyphAdvance;
        commandPos.x = glyphAdvance;
        
        rectBatch1 = new RectangleBatch(1);
        rectBatch2 = new RectangleBatch(5);
    }
    
    void update() {
        timer.update();
        if(Game.tick(20) && cursorIdle) cursorBlink = !cursorBlink; 
        
        if(!prevTyped.equals(typed.toString())) {
            suggest = commands.keySet().stream().anyMatch(name -> name.regionMatches(0, typed.toString(), 0, typed.length())) && typed.length() > 0;
            
            if(suggest) {
                suggestion = commands.keySet().stream()
                        .filter(name -> name.regionMatches(0, typed.toString(), 0, typed.length()))
                        .findFirst()
                        .get();
            }
        }
        
        prevTyped = typed.toString();
    }
    
    void render() {
        rectBatch1.batchStart(1);
            rectBatch1.drawRectangle(0, 0, Window.getWidth(), font.size + 4, Color.BLACK);
        rectBatch1.batchEnd();
        
        rectBatch2.batchStart(0.5f);
            opaqueRectangles.forEach((index, rect) -> {
                rectBatch2.drawRectangle(rect, Color.BLACK);
            });
        rectBatch2.batchEnd();
        
        text.drawString(font, ">", caretPos, Color.WHITE);
        
        for(int i = 0; i <= shiftElements; i++) {
            text.drawOutput(font, cmdOutput, cmdOutput[i], i, executed, opaqueRectangles.get(i));
        }
        
        executed = false;
        
        if(suggest) text.drawString(font, suggestion, commandPos, Color.GRAY);
        if(cursorBlink) text.drawString(font, "_", cursorPos, Color.WHITE);
        
        if(typed.length() > 0) {
            if(validate()) text.drawCommand(font, typed.toString(), commandPos);
            else           text.drawString(font, typed.toString(), commandPos, Color.WHITE);
        }
        
        text.resetStringIndex();
        
        ErrorUtils.checkGLError();
    }
    
    void processKeyInput(int key, int action, int mods) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            cursorIdle  = false;
            cursorBlink = true;
            timer.restart();
            
            keyChars.forEach((k, c) -> {
                if(key == k) insertChar(c.getChar((mods == GLFW_MOD_SHIFT)));
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE -> {
                    if(xIndex > 0) {
                        xIndex--;
                        typed.deleteCharAt(xIndex);
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    }
                }
                
                case GLFW_KEY_RIGHT -> {
                    xIndex++;
                    
                    if(xIndex < typed.length()) {
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    } else {
                        xIndex = typed.length();
                        if(typed.length() > 0) {
                            cursorPos.x = charPos.get(xIndex - 1) + glyphAdvance;
                            scrollX();
                        } else {
                            cursorPos.x = glyphAdvance;
                        }
                    }
                }
                
                case GLFW_KEY_LEFT -> {
                    xIndex--;
                    
                    if(xIndex > 0) {
                        cursorPos.x = charPos.get(xIndex);
                        scrollX();
                    } else {
                        xIndex      = 0;
                        cursorPos.x = glyphAdvance;
                    }
                }
                
                case GLFW_KEY_DOWN -> {
                    if(cmdHistory.size() > 0) {
                        yIndex = (yIndex >= cmdHistory.size() - 1) ? cmdHistory.size() - 1 : yIndex + 1;
                        charPos.clear();
                        if(cmdHistory.get(yIndex) != null) autoComplete(cmdHistory.get(yIndex));
                    }
                }
                
                case GLFW_KEY_UP -> {
                    if(cmdHistory.size() > 0) {
                        yIndex = (yIndex == 0) ? 0 : yIndex - 1;
                        charPos.clear();
                        if(cmdHistory.get(yIndex) != null) autoComplete(cmdHistory.get(yIndex));
                    }
                }
                
                case GLFW_KEY_ENTER -> {
                    execute(typed.toString());
                    typed.delete(0, typed.length());
                    xIndex      = 0;
                    yIndex      = cmdHistory.size();
                    caretPos.x  = 0;
                    cursorPos.x = glyphAdvance;
                    charPos.clear();
                }
                
                case GLFW_KEY_TAB -> {
                    if(suggest) autoComplete(suggestion);
                }
            }
        } else {
            timer.start();
        }
    }
    
    private void insertChar(char c) {
        typed.insert(xIndex, c);
        charPos.put(xIndex, (xIndex * glyphAdvance) + glyphAdvance);
        cursorPos.x = charPos.get(xIndex) + glyphAdvance;
        
        xIndex++;
        
        if(xIndex != typed.length()) {
            for(int i = xIndex; i < typed.length(); i++) {
                charPos.put(i, (i * glyphAdvance) + glyphAdvance);
            }
        }
        
        scrollX();
    }
    
    private boolean validate() {
        if((typed.toString().length() > suggestion.length())) {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length()) &&
                   typed.toString().charAt(suggestion.length()) == ' ';
        } else {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length());
        }
    }
    
    private void autoComplete(String s) {
        typed.delete(0, typed.length());
        xIndex = 0;
        
        for(Character c : s.toCharArray()) {    
            insertChar(c);
        }
    }
    
    private void scrollX() {
        if(typed.length() > 0) {
            int xOffset = 0;
            
            if(charPos.get(charPos.size() - 1) + glyphAdvance > XJGE.getResolutionX() - (glyphAdvance * 2)) {
                xOffset = (XJGE.getResolutionX() - glyphAdvance) - (charPos.get(charPos.size() - 1) + glyphAdvance);
                
                if(cursorPos.x - 8 <= Math.abs(xOffset)) {
                    int x = (xIndex < 1) ? 0 : glyphAdvance;
                    
                    xOffset -= ((cursorPos.x - (glyphAdvance + x)) - Math.abs(xOffset));
                }
            }
            
            cursorPos.x += xOffset;
            caretPos.x   = xOffset;
            commandPos.x = xOffset + glyphAdvance;
        } else {
            charPos.clear();
        }
    }
    
    private void execute(String command) {
        String name = "";
        ArrayList<String> args = new ArrayList<>();
        
        cmdHistory.add(command);
        if(cmdHistory.size() == 33) cmdHistory.remove(0);
        
        if(suggestion.length() > 0 && command.regionMatches(0, suggestion, 0, suggestion.length())) {
            name = command.substring(0, suggestion.length());
        }
        
        if(command.contains(" ")) {
            String s1 = command.substring(command.indexOf(" "), command.length()).replaceAll(" ", "");
            String s2 = "";
            
            for(int i = 0; i < s1.length(); i++) {
                char c = s1.charAt(i);
                
                if(c != ',') s2 += c;
                
                if(c == ',' || i == s1.length() - 1) {
                    args.add(s2);
                    s2 = "";
                }
            }
        }
        
        TerminalOutput output;
        
        if(commands.containsKey(name)) {
            commands.get(name).execute(args);
            output = commands.get(name).getOutput();
        } else {
            output = new TerminalOutput("ERROR: Command not recognized. Check syntax or use help.\n", Color.YELLOW);
        }
        
        if(output != null) {
            shiftElements = (shiftElements == 4) ? 4 : shiftElements + 1;
            
            for(int i = shiftElements; i > -1; i--) {
                if(i > 0) {
                    if(cmdOutput[i - 1] != null) {
                        cmdOutput[i] = cmdOutput[i - 1];
                    }
                } else {
                    cmdOutput[i] = new TerminalOutput(Text.wrap(output.text, Window.getWidth(), font), output.color);
                }
            }
        }
        
        executed = true;
    }
    
    void freeBuffers() {
        if(rectBatch1 != null && rectBatch2 != null) {
            rectBatch1.freeBuffers();
            rectBatch2.freeBuffers();
            
            rectBatch1 = null;
            rectBatch2 = null;
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        cursorIdle = (Boolean) evt.getNewValue();
    }
    
    static class TCCLS extends TerminalCommand {
        
        public TCCLS() {
            super("Clears the terminal output.",

                  "Simply type cls to use. This command contains no additional " + 
                  "arguments.",

                  "cls");
        }

        @Override
        public void execute(List<String> args) {
            shiftElements = -1;
            
            opaqueRectangles.clear();
            for(int i = 0; i < 5; i++) opaqueRectangles.put(i, new Rectangle());
        }
    }
    
}