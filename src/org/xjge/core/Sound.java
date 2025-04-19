package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Created: Jun 22, 2021
 * <p>
 * Supplies the data parsed from an audio file into a new sound object that can 
 * be used by the {@link Audio} class to play sound effects. The engine 
 * supports two formats, 8-bit mono (for sound effects), and 16-bit stereo 
 * (for music). Vorbis .ogg is the preferred file format of this engine.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Sound {

    public final int handle;
    private int channels;
    private int sampleRate;
    
    private final boolean isSongPart;
    
    final String filename;
    
    /**
     * Creates a new sound object from the audio file specified. If the audio 
     * file cannot be found, the engine will instead use a fallback sound in 
     * its place.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     * @param isSongPart if true, the sound will not be included in the audio 
     *                   classes sound collection
     */
    Sound(String filename, boolean isSongPart) {
        this.filename   = filename;
        this.isSongPart = isSongPart;
        
        handle = alGenBuffers();
        
        try(InputStream file = Sound.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            loadSound(file);
        } catch(Exception e) {
            Logger.setDomain("audio");
            Logger.logWarning("Failed to load sound \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadSound(Sound.class.getResourceAsStream("/org/xjge/assets/xjge_beep.ogg"));
        }
        
        if(!isSongPart) addToCollection(filename);
        
        ErrorUtils.checkALError();
    }
    
    /**
     * Creates a new sound object from the audio file specified. If the audio 
     * file cannot be found, the engine will instead use a fallback sound in 
     * its place.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    public Sound(String filename) {
        this(filename, false);
    }
    
     /**
     * Parses the data of the sound file specified and generates a new data 
     * buffer from its contents.
     * 
     * @param file the file to extract sound data from
     */
    private void loadSound(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer soundBuf   = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer channelsBuf = stack.mallocInt(1);
            IntBuffer sampleBuf   = stack.mallocInt(1);
            
            ShortBuffer sound = stb_vorbis_decode_memory(soundBuf, channelsBuf, sampleBuf);
            
            channels   = channelsBuf.get();
            sampleRate = sampleBuf.get();
            
            if(channels == 1) alBufferData(handle, AL_FORMAT_MONO16, sound, sampleRate);
            else              alBufferData(handle, AL_FORMAT_STEREO16, sound, sampleRate);
            
            MemoryUtil.memFree(soundBuf);
            
        } catch(IOException e) {
            Logger.setDomain("audio");
            Logger.logSevere("Failed to parse data from sound file", e);
        }
    }
    
    /**
     * Adds the object to a collection in the {@link Audio} class that will be 
     * used to reload audio data when the current OpenAL context is changed.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    private void addToCollection(String filename) {
        Audio.sounds.put(filename, this);
    }
    
    /**
     * Obtains the amount of channels this sound exhibits. This typically can 
     * be used to determine whether or not a sound is mixed in a mono or stereo 
     * format.
     * 
     * @return the number of channels used by the sound object
     */
    public int getChannels() {
        return channels;
    }
    
    /**
     * Obtains the unaltered sample rate of the sound. Typically in the range 
     * of 44kHz to 48kHz.
     * 
     * @return the sample rate of the sound object in hertz
     */
    public int getSampleRate() {
        return sampleRate;
    }
    
    /**
     * Frees the audio data buffer used by this object.
     */
    public void freeSound() {
        if(!isSongPart) Audio.sounds.remove(filename);
        alDeleteBuffers(handle);
    }
    
}