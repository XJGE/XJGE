package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Keyboard {

    private final Map<Integer, KeyboardKey> keyCharacters;
    
    Keyboard() {
        keyCharacters = new HashMap<Integer, KeyboardKey>() {{
            put(GLFW_KEY_SPACE,      new KeyboardKey(' ', ' '));
            put(GLFW_KEY_APOSTROPHE, new KeyboardKey('\'', '\"'));
            put(GLFW_KEY_COMMA,      new KeyboardKey(',', '<'));
            put(GLFW_KEY_MINUS,      new KeyboardKey('-', '_'));
            put(GLFW_KEY_PERIOD,     new KeyboardKey('.', '>'));
            put(GLFW_KEY_SLASH,      new KeyboardKey('/', '?'));
            put(GLFW_KEY_0, new KeyboardKey('0', ')'));
            put(GLFW_KEY_1, new KeyboardKey('1', '!'));
            put(GLFW_KEY_2, new KeyboardKey('2', '@'));
            put(GLFW_KEY_3, new KeyboardKey('3', '#'));
            put(GLFW_KEY_4, new KeyboardKey('4', '$'));
            put(GLFW_KEY_5, new KeyboardKey('5', '%'));
            put(GLFW_KEY_6, new KeyboardKey('6', '^'));
            put(GLFW_KEY_7, new KeyboardKey('7', '&'));
            put(GLFW_KEY_8, new KeyboardKey('8', '*'));
            put(GLFW_KEY_9, new KeyboardKey('9', '('));
            put(GLFW_KEY_SEMICOLON, new KeyboardKey(';', ':'));
            put(GLFW_KEY_EQUAL,     new KeyboardKey('=', '+'));
            put(GLFW_KEY_A, new KeyboardKey('a', 'A'));
            put(GLFW_KEY_B, new KeyboardKey('b', 'B'));
            put(GLFW_KEY_C, new KeyboardKey('c', 'C'));
            put(GLFW_KEY_D, new KeyboardKey('d', 'D'));
            put(GLFW_KEY_E, new KeyboardKey('e', 'E'));
            put(GLFW_KEY_F, new KeyboardKey('f', 'F'));
            put(GLFW_KEY_G, new KeyboardKey('g', 'G'));
            put(GLFW_KEY_H, new KeyboardKey('h', 'H'));
            put(GLFW_KEY_I, new KeyboardKey('i', 'I'));
            put(GLFW_KEY_J, new KeyboardKey('j', 'J'));
            put(GLFW_KEY_K, new KeyboardKey('k', 'K'));
            put(GLFW_KEY_L, new KeyboardKey('l', 'L'));
            put(GLFW_KEY_M, new KeyboardKey('m', 'M'));
            put(GLFW_KEY_N, new KeyboardKey('n', 'N'));
            put(GLFW_KEY_O, new KeyboardKey('o', 'O'));
            put(GLFW_KEY_P, new KeyboardKey('p', 'P'));
            put(GLFW_KEY_Q, new KeyboardKey('q', 'Q'));
            put(GLFW_KEY_R, new KeyboardKey('r', 'R'));
            put(GLFW_KEY_S, new KeyboardKey('s', 'S'));
            put(GLFW_KEY_T, new KeyboardKey('t', 'T'));
            put(GLFW_KEY_U, new KeyboardKey('u', 'U'));
            put(GLFW_KEY_V, new KeyboardKey('v', 'V'));
            put(GLFW_KEY_W, new KeyboardKey('w', 'W'));
            put(GLFW_KEY_X, new KeyboardKey('x', 'X'));
            put(GLFW_KEY_Y, new KeyboardKey('y', 'Y'));
            put(GLFW_KEY_Z, new KeyboardKey('z', 'Z'));
            put(GLFW_KEY_LEFT_BRACKET,  new KeyboardKey('[', '{'));
            put(GLFW_KEY_BACKSLASH,     new KeyboardKey('\\', '|'));
            put(GLFW_KEY_RIGHT_BRACKET, new KeyboardKey(']', '}'));
            put(GLFW_KEY_GRAVE_ACCENT,  new KeyboardKey('`', '~'));
        }};
    }
    
    public Character getKeyCharacter(int key, int mods) {
        if(keyCharacters.containsKey(key)) {
            return keyCharacters.get(key).getCharacter((mods == GLFW_MOD_SHIFT));
        } else {
            return null;
        }
    }
    
}