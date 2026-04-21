package org.xjge.test;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class Key {

    private final char c;
    private final char C;

    /**
     * Creates a new key object containing both lower and uppercase variants.
     * 
     * @param c the lowercase form of the key character
     * @param C the uppercase form of the key character
     */
    Key(char c, char C) {
        this.c = c;
        this.C = C;
    }

    /**
     * Obtains the character associated with this key depending on whether or 
     * not the shift key was held.
     * 
     * @param shiftHeld if true, the uppercase form of the character will be 
     *                  returned
     * @return the character associated with this key
     */
    char getChar(boolean shiftHeld) {
        return (!shiftHeld) ? c : C;
    }
    
}