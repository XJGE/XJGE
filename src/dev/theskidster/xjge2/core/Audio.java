package dev.theskidster.xjge2.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL11.*;

/**
 * @author J Hoffman
 * Created: Jun 20, 2021
 */

public final class Audio {

    public static final int ALL_SOURCES = 0;
    static final int MAX_SOURCES         = 64;
    private static int prevMusicSourceSample;
    private static int prevMusicSourceState;
    
    private static float soundMasterVolume = 1;
    private static float musicMasterVolume = 1;
    
    private static boolean introFinished;
    
    static Speaker speaker;
    private static String prevMusicSourceSong;
    private static Source musicSource;
    private static Sound currSongBody;
    
    private static final Source[] sources = new Source[MAX_SOURCES];
    
    private static final Map<Integer, Integer> sourceSamples = new HashMap<>();
    private static final Map<Integer, Integer> sourceStates  = new HashMap<>();
    private static final Map<Integer, String> sourceSounds   = new HashMap<>();
    
    private static final Map<Integer, Vector3f> camPos  = new HashMap<>();
    private static final Map<Integer, Vector3f> camDir  = new HashMap<>();
    private static final Map<Integer, Double> distances = new TreeMap<>();
    
    static final Map<String, Sound> sounds = new HashMap<>();
    static final Map<String, Song> songs   = new HashMap<>();
    
    static void init() {
        var prevSources = new HashMap<Integer, Source>();
        
        for(int i = 0; i < sources.length; i++) {
            if(sources[i] != null) prevSources.put(sources[i].handle, sources[i]);
            else                   sources[i] = new Source();
        }
        
        if(!prevSources.isEmpty()) {
            prevSources.forEach((handle, prevSource) -> {
                for(Source currSource : sources) {
                    if(handle == currSource.handle) {
                        currSource = new Source(
                                prevSource,
                                sounds.get(sourceSounds.get(handle)),
                                sourceSamples.get(handle),
                                sourceStates.get(handle));
                    }
                }
            });
        } else {
            for(Source source : sources) {
                sourceStates.put(source.handle, AL_INITIAL);
            }
        }
        
        if(musicSource != null) {
            musicSource = new Source(
                    musicSource,
                    songs.get(prevMusicSourceSong),
                    prevMusicSourceSample,
                    prevMusicSourceState,
                    introFinished);
        } else {
            musicSource = new Source();
        }
    }
    
    private static Source findSource() {
        Source source  = null;
        boolean search = true;
        
        for(int i = 0; search; i++) {
            if(i < MAX_SOURCES) {
                if(sources[i].getState(AL_STOPPED) || sources[i].getState(AL_INITIAL)) {
                    source = sources[i];
                    search = false;
                }
            } else {
                //We'll just fkn take it then!
                var tempSources = new HashMap<Source, Float>();
                for(Source src : sources) {
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
    
    static void updateSourcePositions() {
        for(Source source : sources) {
            if(source != null && source.getState(AL_PLAYING)) {
                int id = findClosestViewport(source.getPosition());
                source.setSourcePosition(camPos.get(id), camDir.get(id));
            }
        }
        
        camPos.clear();
        camDir.clear();
    }
    
    static void setViewportCamData(int id, Vector3f position, Vector3f direction) {
        camPos.put(id, position);
        camDir.put(id, direction);
    }
    
    static void findSourceStates() {
        if(musicSource != null) {
            for(Source source : sources) {
                sourceSamples.put(source.handle, alGetSourcei(source.handle, AL_SAMPLE_OFFSET));
                sourceStates.put(source.handle, alGetSourcei(source.handle, AL_SOURCE_STATE));
            }
            
            prevMusicSourceSample = alGetSourcei(musicSource.handle, AL_SAMPLE_OFFSET);
            prevMusicSourceState  = alGetSourcei(musicSource.handle, AL_SOURCE_STATE);
        }
    }
    
    static void checkIntroFinished() {
        //TODO: provide as package private and call from the game loop
        
        if(alGetSourcei(musicSource.handle, AL_BUFFERS_PROCESSED) == 2 && !introFinished) {
            alSourceUnqueueBuffers(musicSource.handle);
            
            musicSource.queueSound(currSongBody);
            musicSource.setLooping(true);
            
            alSourcePlay(musicSource.handle);
            
            introFinished = true;
        }
    }
    
    public static int playSound(Sound sound, Vector3f position, boolean loop) {
        Source source = findSource();
        
        source.setSound(sound);
        sourceSounds.put(source.handle, sound.filename);
        
        source.setLooping(loop);
        source.setWorldPosition(position);
        
        alSourcePlay(source.handle);
        ErrorUtils.checkALError();
        return source.handle;
    }
    
    public static void playMusic(Song song) {
        alSourceStop(musicSource.handle);
        
        musicSource = new Source();
        
        currSongBody = song.body;

        if(song.intro != null) {
            introFinished = false;
            musicSource.queueSound(song.intro);
            musicSource.queueSound(currSongBody);
        } else {
            introFinished = true;
            musicSource.queueSound(currSongBody);
        }
        
        prevMusicSourceSong = song.intro.filename;
        
        musicSource.setLooping(introFinished);
        
        alSourcePlay(musicSource.handle);
        ErrorUtils.checkALError();
    }
    
    public static void pauseMusic() {
        alSourcePause(musicSource.handle);
    }
    
    public static void resumeMusic() {
        alSourcePlay(musicSource.handle);
    }
    
    public static void stopMusic() {
        alSourceStop(musicSource.handle);
    }
    
    public static float getSoundMasterVolume() {
        return soundMasterVolume;
    }
    
    public static float getMusicMasterVolume() {
        return musicMasterVolume;
    }
    
    public static void setSoundMasterVolume(float masterVolume) {
        soundMasterVolume = masterVolume;
        
        for(Source source : sources) {
            alSourcef(source.handle, AL_GAIN, masterVolume);
        }
    }
    
    public static void setMusicMasterVolume(float masterVolume) {
        musicMasterVolume = masterVolume;
        alSourcef(musicSource.handle, AL_GAIN, masterVolume);
    }
    
    public static void setSourceState(int handle, int state) {
        if(handle == ALL_SOURCES) {
            for(Source source : sources) {
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