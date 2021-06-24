package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: Jun 22, 2021
 */

public final class Song {

    public Sound intro;
    public Sound body;
    
    public Song(String filename) {
        body = new Sound(filename, true);
    }
    
    public Song(String introFilename, String bodyFilename) {
        intro = new Sound(introFilename, true);
        body  = new Sound(bodyFilename, true);
    }
    
}