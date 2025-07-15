package org.xjge.core;

import org.xjge.ui.Rectangle;
import org.xjge.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import static org.xjge.ui.Font.DEFAULT_FONT_SIZE;
import static org.xjge.ui.Font.placeholder;
import org.xjge.ui.Glyph;
import org.xjge.ui.TextEffect;

/**
 * Created: May 23, 2021
 * <br><br>
 * Provides a command line interface that can be used to make state changes to 
 * the application during runtime.
 * <br><br>
 * With debug mode enabled, press {@code SHIFT} + {@code F1} to open or close 
 * the terminal. While the terminal is open, input from the keyboard will be 
 * overridden and {@link Noclip} cannot be used.
 * <p>
 * The terminals functionality can be 
 * {@linkplain XJGE#addCommand(String, TerminalCommand) extended} to support 
 * additional commands to better suit the individual requirements of the 
 * implementation.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Terminal {

    boolean show;
    
    private int idleTime;
    private int cursorIndex;
    private int historyIndex;
    private int shiftOutput = -1;
    private final int glyphAdvance;
    
    private boolean cursorBlink;
    private boolean executed = true;
    
    private final Vector2i caretPosition   = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    private final Vector2i cursorPosition  = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    private final Vector2i commandPosition = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    
    private String suggestion = "";
    
    private final StringBuilder typed = new StringBuilder();
    
    private final HighlightSyntax highlight = new HighlightSyntax();
    
    private static final Rectangle commandLine = new Rectangle();
    private static final Rectangle outputArea  = new Rectangle();
    
    private final TreeMap<String, TerminalCommand> commands;
    private final ArrayList<String> commandHistory  = new ArrayList<>();
    private final TerminalOutput[] commandOutput    = new TerminalOutput[5];
    private final HashMap<Integer, Integer> charPos = new HashMap<>();
    
    private final class HighlightSyntax extends TextEffect {

        private int start;

        @Override
        public void apply(int index, Glyph glyph) {
            if(glyph.character == ' ') start = index;

            switch(glyph.character) {
                default -> glyph.color.copy((start != 0 && index > start) ? Color.YELLOW : Color.CYAN);
                case '(', ')', ',', '<', '>' -> glyph.color.copy(Color.WHITE);
            };
        }

        @Override
        public void reset() {
            start = 0;
        }

    }
    
    /**
     * Creates a new command terminal which can be used to interact directly 
     * with the game engine at runtime.
     * 
     * @param commands the commands the terminal will use
     */
    Terminal(TreeMap<String, TerminalCommand> commands) {
        this.commands = commands;
        
        glyphAdvance = placeholder.getGlyphAdvance('>');
        cursorPosition.x  = glyphAdvance;
        commandPosition.x = glyphAdvance;
    }
    
    /**
     * Completes typing the suggestion provided by the terminal.
     * 
     * @param suggestion the command suggestion to complete
     */
    private void autoComplete(String suggestion) {
        typed.delete(0, typed.length());
        cursorIndex = 0;
        
        for(Character c : suggestion.toCharArray()) {    
            insertChar(c);
        }
    }
    
    /**
     * Executes a terminal command.
     * 
     * @param command the command parsed from the typed string
     */
    private void execute(String command) {
        ArrayList<String> args = new ArrayList<>();
        
        commandHistory.add(command);
        if(commandHistory.size() == 33) commandHistory.remove(0);
        
        String name;
        
        if(command.contains(" ")) {
            name = command.substring(0, command.indexOf(" "));
            
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
        } else {
            name = command;
        }
        
        TerminalOutput output;
        
        if(commands.containsKey(name)) {
            commands.get(name).execute(args);
            output = commands.get(name).getOutput();
            commands.get(name).output = null; //Reset the state of the ouput.
        } else {
            output = new TerminalOutput("ERROR: Command not recognized. Check syntax or type \"help\".\n", Color.RED);
        }
        
        if(output != null) {
            shiftOutput = (shiftOutput == 4) ? 4 : shiftOutput + 1;
            
            for(int i = shiftOutput; i > -1; i--) {
                if(i > 0) {
                    if(commandOutput[i - 1] != null) {
                        commandOutput[i] = commandOutput[i - 1];
                    }
                } else {
                    commandOutput[i] = new TerminalOutput(placeholder.wrap(output.text, Window.getWidth()), output.color);
                }
            }
        }
        
        executed = true;
    }
    
    /**
     * Inserts a character into the command line.
     * 
     * @param c the typed character to add
     */
    private void insertChar(char c) {
        typed.insert(cursorIndex, c);
        charPos.put(cursorIndex, (cursorIndex * glyphAdvance) + glyphAdvance);
        cursorPosition.x = charPos.get(cursorIndex) + glyphAdvance;
        
        cursorIndex++;
        
        if(cursorIndex != typed.length()) {
            for(int i = cursorIndex; i < typed.length(); i++) {
                charPos.put(i, (i * glyphAdvance) + glyphAdvance);
            }
        }
        
        scrollX();
    }
    
    /**
     * Offsets the visible portion of the string typed in the terminal by the 
     * position of the cursor. Used to navigate large strings that extend 
     * beyond the windows width.
     */
    private void scrollX() {
        if(typed.length() > 0) {
            int xOffset = 0;
            
            if(charPos.get(charPos.size() - 1) + glyphAdvance > Window.getWidth() - (glyphAdvance * 2)) {
                xOffset = (Window.getWidth() - glyphAdvance) - (charPos.get(charPos.size() - 1) + glyphAdvance);
                
                if(cursorPosition.x - 8 <= Math.abs(xOffset)) {
                    int x = (cursorIndex < 1) ? 0 : glyphAdvance;
                    
                    xOffset -= ((cursorPosition.x - (glyphAdvance + x)) - Math.abs(xOffset));
                }
            }
            
            cursorPosition.x += xOffset;
            caretPosition.x   = xOffset;
            commandPosition.x = xOffset + glyphAdvance;
        } else {
            charPos.clear();
        }
    }
    
    /**
     * Determines if the string in the command line matches a supported 
     * command.
     * 
     * @return true if the command is recognized by the terminal
     */
    private boolean validate() {
        if(typed.length() == 0) return false;
        
        int spaceIndex = -1;
        
        for(int i = 0; i < typed.length(); i++) {
            if(typed.charAt(i) == ' ') {
                spaceIndex = i;
                break;
            }
        }
        
        String commandName = (spaceIndex == -1) ? typed.toString() : typed.substring(0, spaceIndex);
        
        return commands.containsKey(commandName);
    }
    
    /**
     * Processes input and updates the command terminals interface.
     */
    void update() {
        if(XJGE.tick(20)) {
            if(idleTime < 1) idleTime++;
            else             cursorBlink = !cursorBlink;
        }
        
        suggestion = "";
        
        if(typed.length() > 0) {
            for(String command : commands.keySet()) {
                if(command.length() < typed.length()) continue;

                boolean match = true;
                
                for(int i = 0; i < typed.length(); i++) {
                    if(command.charAt(i) != typed.charAt(i)) {
                        match = false;
                        break;
                    }
                }

                if(match) {
                    suggestion = command;
                    break;
                }
            }
        }
    }
    
    /**
     * Renders the interface to the window.
     */
    void render() {
        commandLine.width  = Window.getWidth();
        commandLine.height = placeholder.size + 2;
        commandLine.render(1, Color.BLACK);
        
        outputArea.positionY = commandLine.height;
        outputArea.width     = Window.getWidth();
        outputArea.height    = 140;
        outputArea.render(0.5f, Color.BLACK);
        
        placeholder.drawString(">", caretPosition.x, caretPosition.y, Color.WHITE, 1f);
        
        for(int i = 0; i <= shiftOutput; i++) {
            if(executed) {
                
            }
            //placeholder.drawOutput(commandOutput, commandOutput[i], i, executed, opaqueRectangles[i]);
        }
        
        executed = false;
        
        if(!suggestion.isEmpty()) placeholder.drawString(suggestion, commandPosition.x, commandPosition.y, Color.GRAY, 1f);
        if(cursorBlink) placeholder.drawString("_", cursorPosition.x, cursorPosition.y, Color.WHITE, 1f);
        
        if(typed.length() > 0) {
            if(validate()) placeholder.drawString(typed.toString(), commandPosition.x, commandPosition.y, highlight);
            else           placeholder.drawString(typed.toString(), commandPosition.x, commandPosition.y, Color.WHITE, 1f);
        }
    }
    
    /**
     * Processes input from the keyboard captured by the game window.
     * 
     * @param key the value supplied by GLFW of a single key on the keyboard
     * @param action an action supplied by GLFW that describes the nature of 
     *               the key press
     * @param mods a value supplied by GLFW denoting whether any mod keys where 
     *             held (such as shift or control)
     */
    void processKeyboardInput(int key, int action, int mods) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            idleTime    = 0;
            cursorBlink = true;
            
            Input.keyChars.forEach((k, c) -> {
                if(key == k) insertChar(c.getChar((mods == GLFW_MOD_SHIFT)));
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE -> {
                    if(cursorIndex > 0) {
                        cursorIndex--;
                        typed.deleteCharAt(cursorIndex);
                        cursorPosition.x = charPos.get(cursorIndex);
                        scrollX();
                    }
                }
                
                case GLFW_KEY_RIGHT -> {
                    cursorIndex++;
                    
                    if(cursorIndex < typed.length()) {
                        cursorPosition.x = charPos.get(cursorIndex);
                        scrollX();
                    } else {
                        cursorIndex = typed.length();
                        if(typed.length() > 0) {
                            cursorPosition.x = charPos.get(cursorIndex - 1) + glyphAdvance;
                            scrollX();
                        } else {
                            cursorPosition.x = glyphAdvance;
                        }
                    }
                }
                
                case GLFW_KEY_LEFT -> {
                    cursorIndex--;
                    
                    if(cursorIndex > 0) {
                        cursorPosition.x = charPos.get(cursorIndex);
                        scrollX();
                    } else {
                        cursorIndex = 0;
                        cursorPosition.x = glyphAdvance;
                    }
                }
                
                case GLFW_KEY_DOWN -> {
                    if(!commandHistory.isEmpty()) {
                        historyIndex = (historyIndex >= commandHistory.size() - 1) ? commandHistory.size() - 1 : historyIndex + 1;
                        charPos.clear();
                        if(commandHistory.get(historyIndex) != null) autoComplete(commandHistory.get(historyIndex));
                    }
                }
                
                case GLFW_KEY_UP -> {
                    if(!commandHistory.isEmpty()) {
                        historyIndex = (historyIndex == 0) ? 0 : historyIndex - 1;
                        charPos.clear();
                        if(commandHistory.get(historyIndex) != null) autoComplete(commandHistory.get(historyIndex));
                    }
                }
                
                case GLFW_KEY_ENTER -> {
                    execute(typed.toString());
                    typed.delete(0, typed.length());
                    cursorIndex  = 0;
                    historyIndex = commandHistory.size();
                    caretPosition.x  = 0;
                    cursorPosition.x = glyphAdvance;
                    charPos.clear();
                }
                
                case GLFW_KEY_TAB -> {
                    if(!suggestion.isEmpty()) autoComplete(suggestion);
                }
            }
        }
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    void freeBuffers() {
        commandLine.delete();
        outputArea.delete();
    }
    
}