package org.xjge.core;

/**
 * Created: Jul 20, 2021
 * <p>
 * Represents a single key on the keyboard.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Key {
    
    private final char c;
    private final char C;

    /**
     * Creates a new Key struct that contains the characters the key 
     * represents.
     * 
     * @param c the default letter/symbol of the key
     * @param C the letter/symbol of the key when shift is held
     */
    Key(char c, char C) {
        this.c = c;
        this.C = C;
    }

    /**
     * Returns the letter/symbol the key represents depending on whether 
     * the shift key is held.
     * 
     * @param shiftHeld if true, the uppercase/secondary character will be returned
     * 
     * @return one of the letters/symbols the key represents
     */
    char getChar(boolean shiftHeld) {
        return (!shiftHeld) ? c : C; 
    }
}