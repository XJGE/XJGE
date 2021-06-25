package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: Jun 22, 2021
 */

public final class Song {

    public Sound intro;
    public Sound body;
    
    public Song(String filename) {
        body = new Sound(filename);
        addToCollection(filename);
    }
    
    public Song(String introFilename, String bodyFilename) {
        intro = new Sound(introFilename);
        body  = new Sound(bodyFilename);
        
        addToCollection(bodyFilename);
    }
    
    private void addToCollection(String filename) {
        Audio.songs.put(filename, this);
    }
    
    public void freeSong() {
        if(intro != null) {
            Audio.songs.remove(intro.filename);
            intro.freeSound();
        }
        
        Audio.songs.remove(body.filename);
        body.freeSound();
    }
    
}