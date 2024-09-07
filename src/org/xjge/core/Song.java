package org.xjge.core;

//Created: Jun 22, 2021

/**
 * Represents a musical composition, may or may not contain an intro which is 
 * played once before entering a looping body section. All {@link Sound} 
 * objects used by this object should be formatted as 16-bit stereo.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Song {

    Sound intro;
    Sound body;
    
    /**
     * Creates a new song object using the audio file specified. The file 
     * provided should be formatted in 16-bit stereo.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    public Song(String filename) {
        body = new Sound(filename, true);
        addToCollection(filename);
    }
    
    /**
     * Creates a new song object using the audio files specified. The files 
     * provided should be formatted in 16-bit stereo. Creating a song with this 
     * constructor ensures that an intro sequence plays before subsequently 
     * looping a body section indefinitely until stopped.
     * 
     * @param introFilename the name of the file to load for the intro section. 
     *                      Expects the file extension to be included.
     * @param bodyFilename the name of the file to load for the body section. 
     *                     Expects the file extension to be included.
     * 
     * @see Audio#playMusic(Song) 
     */
    public Song(String introFilename, String bodyFilename) {
        intro = new Sound(introFilename, true);
        body  = new Sound(bodyFilename, true);
        
        addToCollection(bodyFilename);
    }
    
    /**
     * Adds the object to a collection in the {@link Audio} class that will be 
     * used to reload audio data when the current OpenAL context is changed.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    private void addToCollection(String filename) {
        Audio.songs.put(filename, this);
    }
    
    /**
     * Frees the audio data buffer used by this object.
     */
    public void freeSong() {
        if(intro != null) {
            Audio.songs.remove(intro.filename);
            intro.freeSound();
        }
        
        Audio.songs.remove(body.filename);
        body.freeSound();
    }
    
}