package org.xjge.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_APOSTROPHE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import org.xjge.core.Mouse;
import org.xjge.graphics.Texture;
import org.xjge.ui.Font;
import org.xjge.ui.Icon;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public abstract class UITextInput {

    private boolean hasFocus;
    protected boolean firstIndexSet;
    protected boolean disabled;
    protected boolean showCarat;
    protected boolean caratBlink;
    protected boolean caratIdle;
    
    protected int textPositionX;
    protected int textPositionY;
    
    protected final int HEIGHT  = 28;
    protected final int PADDING = 6;
    
    protected int firstIndex;
    protected int lastIndex;
    
    private int lengthToIndex;
    private int textScrollOffset;
    private int currentIndex;
    private int previousIndex;
    
    protected final Rectangle bounds;
    protected final Rectangle highlight = new Rectangle(0, 0, 0, HEIGHT - 4);
    protected final StringBuilder typed = new StringBuilder();
    protected final Icon carat;
    
    protected final static HashMap<Integer, Key> keyChars;
    
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
    }
    
    /**
     * Creates a new object that can listen to text input using the keyboard.
     * 
     * @param width the width of the visible text area
     */
    UITextInput(int width, Texture iconsTexture) {
        bounds = new Rectangle(0, 0, width, HEIGHT);
        carat  = new Icon(iconsTexture, 24, 24, false);
        carat.setSubImage(6, 0);
    }
    
    /**
     * Obtains the value closest to the target number
     * 
     * @param value1 the first (low) value to test
     * @param value2 the second (high) value to test
     * @param target the target number
     * @return the value that's closest to the target number
     */
    private int getClosest(float value1, float value2, float target) {
        return (int) ((target - value1 >= value2 - target) ? value2 : value1);
    }
    
    /**
     * Searches the string and returns the index of the character closest to the
     * mouse cursors horizontal position.
     * 
     * @param values the indices of the string
     * @param cursorX the horizontal position of the mouse cursor
     * @return the index closest to the cursor
     */
    private int search(int[] values, float cursorX) {
        int n = values.length;
        
        //Hack included to avoid IndexOutOfBoundsException
        if(n == 0) {
            textScrollOffset = 0;
            return 0;
        }
        
        if(cursorX <= values[0])     return values[0];
        if(cursorX >= values[n - 1]) return values[n - 1];
        
        int i   = 0;
        int j   = n;
        int mid = 0;
        
        while(i < j) {
            mid = (i + j) / 2;
            
            if(values[mid] == cursorX) return values[mid]; 
            
            if(cursorX < values[mid]) {
                if(mid > 0 && cursorX > values[mid - 1]) {
                    return getClosest(values[mid - 1], values[mid], cursorX);
                }
                
                j = mid;
            } else {
                if(mid < n - 1 && cursorX < values[mid + 1]) {
                    return getClosest(values[mid], values[mid + 1], cursorX);
                }
                
                i = mid + 1;
            }
        }
        
        return values[mid];
    }
    
    /**
     * Deletes text from the typed string using the length of the text highlight.
     */
    protected void deleteSection() {
        int min = Math.min(firstIndex, lastIndex);
        int max = Math.max(firstIndex, lastIndex);

        typed.replace(min, max, "");

        setIndex(min);
        scrollText();

        highlight.width = 0;
    }
    
    /**
     * Permits text input with the keyboard on the implementing object.
     */
    protected void focus() {
        hasFocus = true;
    }
    
    /**
     * Defines the highlighted range used to determine which characters are 
     * selected from the typed string.
     * 
     * @param cursorX the horizontal position of the mouse cursor
     */
    protected void highlightText(float cursorX) {
        if(typed.length() > 0 && hasFocus) {
            if(cursorX - bounds.positionX - PADDING >= bounds.width - (PADDING * 3)) {
                setIndex((getIndex() > typed.length() - 1) ? typed.length() : getIndex() + 1);
                scrollText();
            }

            if(cursorX - bounds.positionX - PADDING <= (PADDING * 3)) {
                setIndex((getIndex() <= 0) ? 0 : getIndex() - 1);
                scrollText();
            }

            if(!firstIndexSet) {
                firstIndex    = getIndex();
                firstIndexSet = true;
            } else {
                int newIndex = findClosestIndex(cursorX - bounds.positionX - PADDING);
                setIndex(newIndex);
                scrollText();
                
                lastIndex = getIndex();
                
                int firstIndexPosX = Font.FALLBACK.lengthInPixels(typed.substring(0, firstIndex));
                int lastIndexPosX  = Font.FALLBACK.lengthInPixels(typed.substring(0, lastIndex));

                int minX = Math.min(firstIndexPosX, lastIndexPosX);
                int maxX = Math.max(firstIndexPosX, lastIndexPosX);

                highlight.positionX = (minX + bounds.positionX + PADDING) + getTextScrollOffset();
                highlight.width     = (maxX - minX);
            }
        }
    }
    
    /**
     * Inserts the specified character into the typed string at the current 
     * index.
     * 
     * @param c the character to add
     */
    protected void insertChar(char c) {
        typed.insert(currentIndex, c);
        previousIndex = currentIndex;
        currentIndex++;
        scrollText();
    }
    
    /**
     * Used to scroll the text horizontally if it falls outside of the bounds 
     * of the implementing text area.
     */
    protected void scrollText() {
        lengthToIndex = Font.FALLBACK.lengthInPixels(typed.substring(0, currentIndex));
        
        //Hack to prevent scrolling when carat is positioned afted the first character
        if(currentIndex == 1) {
            textScrollOffset = 0;
        } else {
            int result = (int) ((bounds.width - PADDING) - (lengthToIndex + textPositionX - (bounds.positionX + PADDING)));
            
            if(previousIndex < currentIndex) {
                if(carat.position.x >= (bounds.positionX + bounds.width) - (PADDING * 3)) {
                    textScrollOffset = result - PADDING;
                    if(textScrollOffset > 0) textScrollOffset = 0;
                }
            } else {
                if(carat.position.x <= bounds.positionX + (PADDING * 3)) {
                    textScrollOffset = (int) (result - bounds.width + PADDING);
                }
            }
        }
        
        carat.position.x = bounds.positionX + (lengthToIndex + textScrollOffset) + PADDING;
        carat.position.y = bounds.positionY + 4;
    }
    
    /**
     * Sets the current index to place the carat cursor at.
     * 
     * @param index the desired index in the typed string
     */
    protected void setIndex(int index) {
        previousIndex = currentIndex;
        currentIndex  = index;
    }
    
    /**
     * Prohibits text input using the keyboard on the implementing object.
     */
    protected void unfocus() {
        hasFocus      = false;
        showCarat     = false;
        caratBlink    = false;
        caratIdle     = false;
        firstIndexSet = false;
        
        textScrollOffset = 0;
        highlight.width  = 0;
        
        validate();
    }
    
    /**
     * Changes the typed string of the implementing object.
     * 
     * @param text the new value to set the typed string to
     */
    protected final void setText(String text) {
        typed.setLength(0);
        currentIndex = 0;
        for(char c : text.toCharArray()) insertChar(c);
    }
    
    /**
     * Used to determine if the implementing object is able to receive text 
     * input through the keyboard.
     * 
     * @return true if text input is permitted by the implementing object
     */
    protected boolean hasFocus() {
        return hasFocus;
    }
    
    /**
     * Find the index in the typed string closest to the mouse cursor.
     * 
     * @param cursorX the horizontal position of the mouse cursor
     * @return the index closest to the cursor
     */
    protected int findClosestIndex(float cursorX) {
        if(typed.length() <= 1) {
            int charWidth = Font.FALLBACK.lengthInPixels(typed.toString());
            return (cursorX < (charWidth / 2)) ? 0 : 1;
        }
        
        List<Integer> culled = new ArrayList<>();
        
        //Remove numbers that are outside of the carats range
        for(int i = 0; i < typed.length() + 1; i++) {
            int position = Font.FALLBACK.lengthInPixels(typed.substring(0, i)) + textScrollOffset;
            
            if(position >= 0 && position < bounds.width) {
                culled.add(position);
            }
        }
        
        int[] values = culled.stream().mapToInt(Integer::intValue).toArray();
        int result   = 0;
        
        for(int i = 0; i < typed.length() + 1; i++) {
            if(Font.FALLBACK.lengthInPixels(typed.substring(0, i)) + textScrollOffset == search(values, cursorX)) {
                result = i;
            }
        }
        
        return result;
    }
    
    /**
     * Obtains the current index that the carat cursor is placed at.
     * 
     * @return the index of the carat cursor
     */
    protected int getIndex() {
        return currentIndex;
    }
    
    /**
     * Obtains a number used to offset the horizontal position of the typed 
     * string inside the text area bounds.
     * 
     * @return the horizontal offset (in pixels) of the typed string
     */
    protected int getTextScrollOffset() {
        return textScrollOffset;
    }
    
    /**
     * Used to organize time-sensitive logic.
     */
    abstract void update();
    
    /**
     * Used to organize draw calls made by OpenGL.
     * 
     * @param uiProgram a shader program used to render the applications UI
     * @param font the font to use for text rendering operations
     */
    abstract void render();
    
    /**
     * Used to dynamically reposition this component whenever the parent 
     * containers position has changed.
     * 
     * @param parentX the horizontal position of the parent container (in pixels)
     * @param parentY the vertical position of the parent container (in pixels)
     * @param offsetX a supplemental value to offset this components horizontal 
     *                position by (in pixels)
     * @param offsetY a supplemental value to offset this components vertical 
     *                position by (in pixels)
     */
    abstract void relocate(Rectangle parent, int offsetX, int offsetY);
    
    /**
     * Called anytime input is performed using the keyboard.
     * 
     * @param key a number provided by GLFW used to identify which key was pressed
     * @param action used to denote the type of action performed on the key 
     *               (GLFW_PRESS, GLFW_RELEASE, etc.)
     * @param mods a number provided by GLFW that indicates whether or not any 
     *             modifier key (shift, alt, etc.) was in use concurrently
     * @return a new command object if an action was performed by the user
     */
    abstract String processKeyInput(int key, int action, int mods);
    
    /**
     * Called anytime input is performed using the mouse.
     * 
     * @param mouse an object containing the input state of the users mouse
     * @return a new command object if an action was performed by the user
     */
    abstract String processMouseInput(Mouse mouse);
    
    /**
     * Validates the input performed by the user.
     */
    abstract void validate();
    
    /**
     * Frees any resources that might've been allocated by this object.
     */
    abstract void destroy();
    
    
}