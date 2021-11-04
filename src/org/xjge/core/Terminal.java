package org.xjge.core;

import org.xjge.graphics.Rectangle;
import org.xjge.graphics.RectangleBatch;
import static org.xjge.core.Font.DEFAULT_SIZE;
import org.xjge.graphics.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

//Created: May 23, 2021

/**
 * Provides a command line through which the engine may be interacted with at
 * runtime.
 * <p>
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
    private static final Rectangle[] opaqueRectangles = new Rectangle[5];
    
    TreeMap<String, TerminalCommand> commands;
    private final ArrayList<String> cmdHistory      = new ArrayList<>();
    private final TerminalOutput[] cmdOutput        = new TerminalOutput[5];
    private final HashMap<Integer, Integer> charPos = new HashMap<>();
    
    static {
        for(int i = 0; i < 5; i++) opaqueRectangles[i] = new Rectangle();
    }
    
    /**
     * Creates a new command terminal which can be used to interact directly 
     * with the game engine at runtime.
     * 
     * @param commands the commands the terminal will use
     * @param font     the font supplied by the engine that the interface will 
     *                 be rendered in
     */
    Terminal(TreeMap<String, TerminalCommand> commands, Font font) {
        this.commands = commands;
        this.font     = font;
        
        glyphAdvance = (int) font.getGlyphAdvance('>');
        cursorPos.x  = glyphAdvance;
        commandPos.x = glyphAdvance;
        
        rectBatch1 = new RectangleBatch(1);
        rectBatch2 = new RectangleBatch(5);
    }
    
    /**
     * Processes input and updates the command terminals interface.
     */
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
    
    /**
     * Renders the interface to the window.
     */
    void render() {
        rectBatch1.batchStart(1);
            rectBatch1.drawRectangle(0, 0, Window.getWidth(), font.size + 4, Color.BLACK);
        rectBatch1.batchEnd();
        
        rectBatch2.batchStart(0.5f);
            for(Rectangle opaqueRectangle : opaqueRectangles) {
                rectBatch2.drawRectangle(opaqueRectangle, Color.BLACK);
            }
        rectBatch2.batchEnd();
        
        text.drawString(font, ">", caretPos, Color.WHITE);
        
        for(int i = 0; i <= shiftElements; i++) {
            text.drawOutput(font, cmdOutput, cmdOutput[i], i, executed, opaqueRectangles[i]);
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
    
    /**
     * Processes input from the keyboard captured by the game window.
     * 
     * @param key    the value supplied by GLFW of a single key on the keyboard
     * @param action an action supplied by GLFW that describes the nature of 
     *               the key press
     * @param mods   a value supplied by GLFW denoting whether any mod keys 
     *               where held (such as shift or control)
     */
    void processKeyInput(int key, int action, int mods) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            cursorIdle  = false;
            cursorBlink = true;
            timer.restart();
            
            Input.keyChars.forEach((k, c) -> {
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
    
    /**
     * Inserts a character into the command line.
     * 
     * @param c the typed character to add
     */
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
    
    /**
     * Determines if the string in the command line matches a supported 
     * command.
     * 
     * @return true if the command is recognized by the terminal
     */
    private boolean validate() {
        if((typed.toString().length() > suggestion.length())) {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length()) &&
                   typed.toString().charAt(suggestion.length()) == ' ';
        } else {
            return typed.toString().regionMatches(0, suggestion, 0, suggestion.length());
        }
    }
    
    /**
     * Completes typing the suggestion provided by the terminal.
     * 
     * @param suggestion the command suggestion to complete
     */
    private void autoComplete(String suggestion) {
        typed.delete(0, typed.length());
        xIndex = 0;
        
        for(Character c : suggestion.toCharArray()) {    
            insertChar(c);
        }
    }
    
    /**
     * Offsets the visible portion of the string typed in the terminal by the 
     * position of the cursor. Used to navigate large strings that extend 
     * beyond the windows width.
     */
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
    
    /**
     * Executes a terminal command.
     * 
     * @param command the command parsed from the typed string
     */
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
            output = new TerminalOutput("ERROR: Command not recognized. Check syntax or use help.\n", Color.RED);
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
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
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
    
    /**
     * Clears the terminal output.
     */
    static final class TCCLS extends TerminalCommand {
        
        /**
         * Creates a new instance of the cls command.
         */
        TCCLS() {
            super("Clears the terminal output.",

                  "Simply type cls to use. This command contains no additional " + 
                  "arguments.",

                  "cls");
        }

        @Override
        public void execute(List<String> args) {
            shiftElements = -1;
            
            for(int i = 0; i < 5; i++) {
                opaqueRectangles[i].xPos   = 0;
                opaqueRectangles[i].yPos   = 0;
                opaqueRectangles[i].width  = 0;
                opaqueRectangles[i].height = 0;
            }
        }
    }
    
}