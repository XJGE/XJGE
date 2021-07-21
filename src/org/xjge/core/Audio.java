package org.xjge.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL11.*;

//Created: Jun 20, 2021

/**
 * Provides an abstraction of the OpenAL API through which sound effects and 
 * music may be played.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Audio {

    /**
     * A unique value that can be passed to the 
     * {@link #setSoundSourceState(int, int)} method to change the current 
     * state of every sound source.
     */
    public static final int ALL_SOURCES = 0;
    
    static final int MAX_SOURCES = 64;
    private static int prevMusicSourceSample;
    private static int prevMusicSourceState;
    
    private static float soundMasterVolume = 1;
    private static float musicMasterVolume = 1;
    
    private static boolean introFinished;
    
    static Speaker speaker;
    private static String prevMusicSourceSong;
    private static SoundSource musicSource;
    private static Sound currSongBody;
    
    private static final SoundSource[] soundSources = new SoundSource[MAX_SOURCES];
    
    private static final Map<Integer, Integer> sourceSamples = new HashMap<>();
    private static final Map<Integer, Integer> sourceStates  = new HashMap<>();
    private static final Map<Integer, String> sourceSounds   = new HashMap<>();
    
    private static final Map<Integer, Vector3f> camPos  = new HashMap<>();
    private static final Map<Integer, Vector3f> camDir  = new HashMap<>();
    private static final Map<Integer, Double> distances = new TreeMap<>();
    
    static final Map<String, Sound> sounds = new HashMap<>();
    static final Map<String, Song> songs   = new HashMap<>();
    
    /**
     * Ensures that the state of the previous OpenAL context is retained 
     * between configuration changes of the current audio output device.
     */
    static void applyContextConfig() {
        sounds.forEach((filename, sound) -> {
            sound = new Sound(filename);
        });
        
        songs.forEach((filename, song) -> {
            song = (song.intro != null) 
                 ? new Song(song.intro.filename, song.body.filename)
                 : new Song(song.body.filename);
        });
        
        var prevSources = new HashMap<Integer, SoundSource>();
        
        for(int i = 0; i < soundSources.length; i++) {
            if(soundSources[i] != null) prevSources.put(soundSources[i].handle, soundSources[i]);
            else                   soundSources[i] = new SoundSource();
        }
        
        if(!prevSources.isEmpty()) {
            prevSources.forEach((handle, prevSource) -> {
                for(SoundSource currSource : soundSources) {
                    if(handle == currSource.handle) {
                        currSource = new SoundSource(
                                prevSource,
                                sounds.get(sourceSounds.get(handle)),
                                sourceSamples.get(handle),
                                sourceStates.get(handle));
                    }
                }
            });
        } else {
            for(SoundSource source : soundSources) {
                sourceStates.put(source.handle, AL_INITIAL);
            }
        }
        
        if(musicSource != null) {
            musicSource = new SoundSource(
                    musicSource,
                    songs.get(prevMusicSourceSong),
                    prevMusicSourceSample,
                    prevMusicSourceState,
                    introFinished);
        } else {
            musicSource = new SoundSource();
        }
    }
    
    /**
     * Queries a pool of {@link SoundSource} objects and returns one which is 
     * not currently in use. 
     * <p>
     * NOTE: If no sound sources are available, the engine will attempt to 
     * procure one by returning whichever is playing audio at the lowest 
     * volume.
     * </p>
     * 
     * @return a sound source object to use for audio output
     */
    private static SoundSource findSoundSource() {
        SoundSource source  = null;
        boolean search = true;
        
        for(int i = 0; search; i++) {
            if(i < MAX_SOURCES) {
                if(soundSources[i].getState(AL_STOPPED) || soundSources[i].getState(AL_INITIAL)) {
                    source = soundSources[i];
                    search = false;
                }
            } else {
                //We'll just fkn take it then!
                var tempSources = new HashMap<SoundSource, Float>();
                for(SoundSource src : soundSources) {
                    tempSources.put(src, alGetSourcef(src.handle, AL_GAIN));
                }
                
                //Return the source with the lowest volume.
                source = tempSources.entrySet().stream()
                        .min(Comparator.comparingDouble(Map.Entry::getValue))
                        .get()
                        .getKey();
                
                alSourceStop(source.handle);
                search = false;
            }
        }
        
        return source;
    }
    
    /**
     * Returns the ID number of the {@link Viewport} whos camera object is 
     * positioned closest to the location of a {@link SoundSource} in the game 
     * world.
     * 
     * @param position the position of the sound source object
     * 
     * @return a value used to identify a viewport
     */
    private static int findClosestViewport(Vector3f position) {
        for(int i = 0; i < camDir.size(); i++) {
            if(position != null) distances.put(i, Math.sqrt(position.distance(camPos.get(i))));
            else                 distances.put(i, 0.0);
        }
        
        return distances.entrySet().stream()
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .get()
                .getKey();
    }
    
    /**
     * Repositions every active {@link SoundSource} object around the single 
     * OpenAL listener object located at the center of the game world.
     * 
     * @see SoundSource#setSourcePosition(Vector3f, Vector3f)
     */
    static void updateSoundSourcePositions() {
        for(SoundSource source : soundSources) {
            if(source != null && source.getState(AL_PLAYING)) {
                int id = findClosestViewport(source.getPosition());
                source.setSourcePosition(camPos.get(id), camDir.get(id));
            }
        }
        
        camPos.clear();
        camDir.clear();
    }
    
    /**
     * Captures the orientation of a viewports camera object. This data will be
     * used to calculate the positions of sound sources within the game world 
     * relative to players point of view.
     * 
     * @param viewportID a value used to identify the viewport
     * @param position   the current position of the viewports camera object
     * @param direction  the direction in which the viewports camera is 
     *                   currently facing
     */
    static void setViewportCamData(int viewportID, Vector3f position, Vector3f direction) {
        camPos.put(viewportID, position);
        camDir.put(viewportID, direction);
    }
    
    /**
     * Captures the state of each sound source object currently in use. The 
     * captured state of the sound sources will be transferred to a new OpenAL 
     * context once it is initialized.
     * 
     * @see #applyContextConfig()
     */
    static void saveContextConfig() {
        if(musicSource != null) {
            for(SoundSource source : soundSources) {
                sourceSamples.put(source.handle, alGetSourcei(source.handle, AL_SAMPLE_OFFSET));
                sourceStates.put(source.handle, alGetSourcei(source.handle, AL_SOURCE_STATE));
            }
            
            prevMusicSourceSample = alGetSourcei(musicSource.handle, AL_SAMPLE_OFFSET);
            prevMusicSourceState  = alGetSourcei(musicSource.handle, AL_SOURCE_STATE);
        }
    }
    
    /**
     * Queues the main body section of a piece of music once the intro sequence 
     * has finished. The body section will loop indefinitely until paused, 
     * stopped, or another piece of music is played.
     */
    static void queueMusicBodySection() {
        if(alGetSourcei(musicSource.handle, AL_BUFFERS_PROCESSED) == 2 && !introFinished) {
            alSourceUnqueueBuffers(musicSource.handle);
            
            musicSource.queueSound(currSongBody);
            musicSource.setLooping(true);
            
            alSourcePlay(musicSource.handle);
            
            introFinished = true;
        }
    }
    
    /**
     * Emits a {@link Sound} from some specified position in the game world.
     * 
     * @param sound    the sound to play
     * @param position the position from which the sound will play. If 
     *                 <b>null</b> is passed, the sound will not be attenuated 
     *                 according to its position in the game world.
     * @param loop     if true, the sound will loop indefinitely until stopped
     * 
     * @return a value that identifies which {@link SoundSource} was used to 
     *         play the sound
     * 
     * @see #setSoundSourceState(int, int)
     */
    public static int playSound(Sound sound, Vector3f position, boolean loop) {
        SoundSource source = findSoundSource();
        
        source.setSound(sound);
        sourceSounds.put(source.handle, sound.filename);
        
        source.setLooping(loop);
        source.setWorldPosition(position);
        
        alSourcePlay(source.handle);
        ErrorUtils.checkALError();
        return source.handle;
    }
    
    /**
     * Plays a song from the beginning. If the song contains an intro 
     * sequence it will be played first before looping the body section.
     * <p>
     * NOTE: Calling this method will interrupt and replace any song that was 
     * previously playing.
     * </p>
     * 
     * @param song the song to start playing
     */
    public static void playMusic(Song song) {
        alSourceStop(musicSource.handle);
        
        musicSource = new SoundSource();
        
        currSongBody = song.body;

        if(song.intro != null) {
            introFinished = false;
            musicSource.queueSound(song.intro);
            musicSource.queueSound(currSongBody);
        } else {
            introFinished = true;
            musicSource.queueSound(currSongBody);
        }
        
        prevMusicSourceSong = song.body.filename;
        
        musicSource.setLooping(introFinished);
        
        alSourcePlay(musicSource.handle);
        ErrorUtils.checkALError();
    }
    
    /**
     * Pauses whatever song is currently playing at its current measure.
     * 
     * @see #resumeMusic()
     */
    public static void pauseMusic() {
        alSourcePause(musicSource.handle);
    }
    
    /**
     * Resumes playing whatever song was previously paused from where it left 
     * off.
     * 
     * @see #pauseMusic()
     */
    public static void resumeMusic() {
        alSourcePlay(musicSource.handle);
    }
    
    /**
     * Ceases playing a song. Unlike {@link #pauseMusic()}, stopping a song 
     * will set reset its measure.
     */
    public static void stopMusic() {
        alSourceStop(musicSource.handle);
    }
    
    /**
     * Obtains the current value of the sound master volume.
     * <p>
     * NOTE: It's encouraged that you multiply this value by some factor of ten
     * to obfuscate the implementations reliance on a floating point value from 
     * 0 to 1 and instead provide players with something more practical 
     * (0 to 100, etc).
     * </p>
     * 
     * @return the value used to attenuate the volume of all sound effects
     */
    public static float getSoundMasterVolume() {
        return soundMasterVolume;
    }
    
    /**
     * Provides the current value of the music master volume.
     * <p>
     * NOTE: It's encouraged that you multiply this value by some factor of ten
     * to obfuscate the implementations reliance on a floating point value from 
     * 0 to 1 and instead provide players with something more practical 
     * (0 to 100, etc).
     * </p>
     * 
     * @return the value used to attenuate the volume of the games background 
     *         music
     */
    public static float getMusicMasterVolume() {
        return musicMasterVolume;
    }
    
    /**
     * Sets the master volume that will be used to attenuate the overall volume
     * of all sound effects.
     * 
     * @param masterVolume the value used to attenuate the volume of sound 
     *                     effects (between 0 and 1)
     */
    public static void setSoundMasterVolume(float masterVolume) {
        if(masterVolume >= 0 && masterVolume <= 1) {
            soundMasterVolume = masterVolume;
            
            for(SoundSource source : soundSources) {
                alSourcef(source.handle, AL_GAIN, masterVolume);
            }
        }
    }
    
    /**
     * Sets the master volume that will be used to attenuate the overall volume
     * of the games background music.
     * 
     * @param masterVolume the value used to attenuate the volume of the games 
     *                     music (between 0 and 1)
     */
    public static void setMusicMasterVolume(float masterVolume) {
        if(masterVolume >= 0 && masterVolume <= 1) {
            musicMasterVolume = masterVolume;
            alSourcef(musicSource.handle, AL_GAIN, masterVolume);
        }
    }
    
    /**
     * Explicitly sets the state of a {@link SoundSource} object.
     * 
     * @param handle the unique handle used to identify the sound source or 
     *               {@link ALL_SOURCES}
     * @param state  the state to set the source to. One of 
     *               {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PLAYING}, 
     *               {@link org.lwjgl.openal.AL10#AL_PAUSED AL_PAUSED}, or 
     *               {@link org.lwjgl.openal.AL10#AL_PAUSED AL_STOPPED}. 
     */
    public static void setSoundSourceState(int handle, int state) {
        if(handle == ALL_SOURCES) {
            for(SoundSource source : soundSources) {
                switch(state) {
                    case AL_PLAYING -> {
                        if(sourceStates.get(source.handle) == AL_PAUSED || source.getState(AL_PAUSED)) {
                            alSourcePlay(source.handle);
                        }
                    }
                    
                    case AL_PAUSED -> {
                        if(sourceStates.get(source.handle) == AL_PLAYING || source.getState(AL_PLAYING)) {
                            alSourcePause(source.handle);
                        }
                    }
                        
                    case AL_STOPPED -> alSourceStop(source.handle);
                }
            }
        } else {
            if(handle > 0 && handle <= MAX_SOURCES) {
                switch(state) {
                    case AL_PLAYING -> {
                        if(sourceStates.get(handle) == AL_PAUSED || alGetSourcei(handle, AL_SOURCE_STATE) == AL_PAUSED) {
                            alSourcePlay(handle);
                        }
                    }
                        
                    case AL_PAUSED -> {
                        if(sourceStates.get(handle) == AL_PLAYING || alGetSourcei(handle, AL_SOURCE_STATE) == AL_PLAYING) {
                            alSourcePause(handle);
                        }
                    }
                        
                    case AL_STOPPED -> alSourceStop(handle);
                }
            } else {
                Logger.logWarning("Could not find source by the handle of " + handle + ".", null);
            }
        }
        
        ErrorUtils.checkALError();
    }
    
}