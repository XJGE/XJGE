package org.xjge.core;

import org.xjge.ui.Rectangle;
import org.xjge.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;
import org.xjge.ui.Font;
import static org.xjge.ui.Font.DEFAULT_FONT_SIZE;
import org.xjge.ui.Glyph;
import org.xjge.ui.TextEffect;

/**
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
    private boolean cursorBlink;
    
    private int idleTime;
    private int cursorIndex;
    private int historyIndex;
    private final int glyphAdvance;
    
    private String suggestion = "";
    private String parsedCommandName;
    
    private final Vector2i caretPosition    = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    private final Vector2i cursorPosition   = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    private final Vector2i commandPosition  = new Vector2i(0, DEFAULT_FONT_SIZE / 4);
    private final StringBuilder typed       = new StringBuilder();
    private final HighlightSyntax highlight = new HighlightSyntax();
    private final Rectangle commandLine     = new Rectangle();
    private final Rectangle outputArea      = new Rectangle();
    final TerminalScrollBar scrollBar       = new TerminalScrollBar(outputArea);
    
    private final TreeMap<String, TerminalCommand> commands;
    
    private final List<String> parsedCommandArgs = new ArrayList<>();
    private final List<String> commandHistory    = new ArrayList<>();
    private final List<TerminalOutput> output    = new ArrayList<>();
    
    private final HashMap<Integer, Integer> glyphPositions = new HashMap<>();
    
    private final class HighlightSyntax extends TextEffect {

        private int start;

        @Override
        public void apply(int index, Glyph glyph) {
            if(glyph.character == ' ') start = index;

            switch(glyph.character) {
                default -> glyph.color.copy((start != 0 && index > start) ? Color.YELLOW : Color.CYAN);
                case '(', ')', ',', '<', '>' -> glyph.color.copy(Color.WHITE);
            }
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
        
        glyphAdvance = Font.fallback.getGlyphAdvance('>');
        cursorPosition.x  = glyphAdvance;
        commandPosition.x = glyphAdvance;
        
        relocate(Window.getWidth(), Window.getHeight());
    }
    
    /**
     * Completes typing the suggestion provided by the terminal.
     * 
     * @param suggestion the command suggestion to complete
     */
    private void autoComplete(String suggestion) {
        typed.delete(0, typed.length());
        cursorIndex = 0;
        
        for(int i = 0; i < suggestion.length(); i++) {
            insertChar(suggestion.charAt(i));
        }
    }
    
    /**
     * Executes a terminal command.
     */
    private void executeCommand() {
        String command = typed.toString();
        
        commandHistory.add(command);
        if(commandHistory.size() > 64) commandHistory.remove(0);
        
        TerminalOutput commandOutput;
        
        if(commands.containsKey(parsedCommandName)) {
            commandOutput = commands.get(parsedCommandName).execute(parsedCommandArgs);
        } else {
            commandOutput = new TerminalOutput("ERROR: Command \"" + parsedCommandName + "\" not recognized. Please check syntax.", Color.RED);
        }
        
        if(commandOutput != null) output.add(commandOutput);
        
        formatOutput();
    }
    
    private void formatOutput() {
        int outputLength = 0;
        
        for(TerminalOutput commandOutput : output) {
            commandOutput.lines.clear();
            var newLines = Font.fallback.split(commandOutput.text, outputArea.width - 40);
            
            for(int i = 0; i < newLines.size(); i++) {
                commandOutput.lines.add((i == 0) ? newLines.get(i) : "  " + newLines.get(i));
                outputLength += Font.fallback.size;
            }
        }
        
        scrollBar.setContentAreaLength(outputLength);
    }
    
    /**
     * Inserts a character into the command line.
     * 
     * @param c the typed character to add
     */
    private void insertChar(char c) {
        typed.insert(cursorIndex, c);
        glyphPositions.put(cursorIndex, (cursorIndex * glyphAdvance) + glyphAdvance);
        cursorPosition.x = glyphPositions.get(cursorIndex) + glyphAdvance;
        
        cursorIndex++;
        
        if(cursorIndex != typed.length()) {
            for(int i = cursorIndex; i < typed.length(); i++) {
                glyphPositions.put(i, (i * glyphAdvance) + glyphAdvance);
            }
        }
        
        parseCommand();
        scrollCommandLine();
    }
    
    private void parseCommand() {
        if(typed.length() == 0) return;
        
        int spaceStart = -1;
        
        for(int i = 0; i < typed.length(); i++) {
            if(typed.charAt(i) == ' ') {
                spaceStart = i;
                break;
            }
        }
        
        if(spaceStart == -1) {
            parsedCommandName = typed.toString();
            parsedCommandArgs.clear();
        } else {
            parsedCommandName = typed.toString().substring(0, spaceStart);
            parsedCommandArgs.clear();
            
            int argStart = spaceStart + 1;
            int argIndex = argStart;

            while(argIndex <= typed.length()) {
                if(argIndex == typed.length() || typed.charAt(argIndex) == ',') {
                    if(argStart < argIndex) {
                        while(argStart < argIndex && typed.charAt(argStart) == ' ') argStart++;
                        while(argIndex > argStart && typed.charAt(argIndex - 1) == ' ') argIndex--;

                        if(argStart < argIndex) {
                            parsedCommandArgs.add(typed.toString().substring(argStart, argIndex));
                        }
                    }
                    argStart = argIndex + 1;
                }
                argIndex++;
            }
        }
    }
    
    /**
     * Offsets the visible portion of the string typed in the terminal by the 
     * position of the cursor. Used to navigate large strings that extend 
     * beyond the windows width.
     */
    private void scrollCommandLine() {
        if(typed.length() > 0) {
            int xOffset = 0;
            
            if(glyphPositions.get(glyphPositions.size() - 1) + glyphAdvance > Window.getWidth() - (glyphAdvance * 2)) {
                xOffset = (Window.getWidth() - glyphAdvance) - (glyphPositions.get(glyphPositions.size() - 1) + glyphAdvance);
                
                if(cursorPosition.x - 8 <= Math.abs(xOffset)) {
                    int x = (cursorIndex < 1) ? 0 : glyphAdvance;
                    
                    xOffset -= ((cursorPosition.x - (glyphAdvance + x)) - Math.abs(xOffset));
                }
            }
            
            cursorPosition.x += xOffset;
            caretPosition.x   = xOffset;
            commandPosition.x = xOffset + glyphAdvance;
        } else {
            glyphPositions.clear();
        }
    }
    
    /**
     * Determines if the string in the command line matches a supported 
     * command.
     * 
     * @return true if the command is recognized by the terminal
     */
    private boolean validateCommand() {
        return parsedCommandName.length() > 0 && commands.containsKey(parsedCommandName);
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
        outputArea.render(0.5f, Color.BLACK);
        
        int yOffset = ((outputArea.positionY + 6) - Font.fallback.size) - scrollBar.getContentOffset();
        
        glEnable(GL_SCISSOR_TEST);
        glScissor(outputArea.positionX, outputArea.positionY, outputArea.width, outputArea.height);
        
        for(int i = output.size() - 1; i >= 0; i--) {
            var commandOutput = output.get(i);
            
            for(int j = commandOutput.lines.size() - 1; j >= 0; j--) {
                yOffset += Font.fallback.size;
                Font.fallback.drawString(commandOutput.lines.get(j), 12, yOffset, commandOutput.color, 1f);
            }
        }
        
        glDisable(GL_SCISSOR_TEST);
        
        scrollBar.render();
        commandLine.render(1, Color.BLACK);
        
        Font.fallback.drawString(">", caretPosition.x, caretPosition.y, Color.WHITE, 1f);
        
        if(!suggestion.isEmpty()) Font.fallback.drawString(suggestion, commandPosition.x, commandPosition.y, Color.GRAY, 1f);
        if(cursorBlink) Font.fallback.drawString("_", cursorPosition.x, cursorPosition.y, Color.WHITE, 1f);
        
        if(typed.length() > 0) {
            if(validateCommand()) Font.fallback.drawString(typed, commandPosition.x, commandPosition.y, highlight);
            else Font.fallback.drawString(typed, commandPosition.x, commandPosition.y, Color.WHITE, 1f);
        }
    }
    
    void relocate(int windowWidth, int windowHeight) {
        commandLine.width  = windowWidth;
        commandLine.height = Font.fallback.size + 2;
        
        outputArea.positionY = commandLine.height;
        outputArea.width     = windowWidth;
        outputArea.height    = 140;
        
        scrollBar.relocate();
        
        formatOutput();
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
                if(key == k) insertChar(c.getCharacter((mods == GLFW_MOD_SHIFT)));
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE -> {
                    if(cursorIndex > 0) {
                        cursorIndex--;
                        typed.deleteCharAt(cursorIndex);
                        cursorPosition.x = glyphPositions.get(cursorIndex);
                        parseCommand();
                        scrollCommandLine();
                    }
                }
                
                case GLFW_KEY_RIGHT -> {
                    cursorIndex++;
                    
                    if(cursorIndex < typed.length()) {
                        cursorPosition.x = glyphPositions.get(cursorIndex);
                        scrollCommandLine();
                    } else {
                        cursorIndex = typed.length();
                        if(typed.length() > 0) {
                            cursorPosition.x = glyphPositions.get(cursorIndex - 1) + glyphAdvance;
                            scrollCommandLine();
                        } else {
                            cursorPosition.x = glyphAdvance;
                        }
                    }
                }
                
                case GLFW_KEY_LEFT -> {
                    cursorIndex--;
                    
                    if(cursorIndex > 0) {
                        cursorPosition.x = glyphPositions.get(cursorIndex);
                        scrollCommandLine();
                    } else {
                        cursorIndex = 0;
                        cursorPosition.x = glyphAdvance;
                    }
                }
                
                case GLFW_KEY_DOWN -> {
                    if(!commandHistory.isEmpty()) {
                        historyIndex = (historyIndex >= commandHistory.size() - 1) ? commandHistory.size() - 1 : historyIndex + 1;
                        glyphPositions.clear();
                        if(commandHistory.get(historyIndex) != null) autoComplete(commandHistory.get(historyIndex));
                    }
                }
                
                case GLFW_KEY_UP -> {
                    if(!commandHistory.isEmpty()) {
                        historyIndex = (historyIndex == 0) ? 0 : historyIndex - 1;
                        glyphPositions.clear();
                        if(commandHistory.get(historyIndex) != null) autoComplete(commandHistory.get(historyIndex));
                    }
                }
                
                case GLFW_KEY_ENTER -> {
                    executeCommand();
                    typed.delete(0, typed.length());
                    cursorIndex  = 0;
                    historyIndex = commandHistory.size();
                    caretPosition.x  = 0;
                    cursorPosition.x = glyphAdvance;
                    glyphPositions.clear();
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