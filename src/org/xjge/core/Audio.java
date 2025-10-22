package org.xjge.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.AL_INITIAL;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcIsExtensionPresent;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Provides a single point of access through which sound effects and music can be played.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Audio {
    
    static final int MIN_SOURCES = 64;
    static final int MAX_SOURCES = 256;
    
    private static Speaker speaker;
    
    private static final SoundSource[] sourcePool = new SoundSource[MAX_SOURCES];
    
    private static final Map<String, Sound> sounds = new HashMap<>();
    
    private static final Map<Integer, Vector3f> cameraPositions  = new HashMap<>();
    private static final Map<Integer, Vector3f> cameraDirections = new HashMap<>();
    private static final Map<Integer, Double> distances = new HashMap<>();
    
    private static final NavigableMap<Integer, Speaker> speakers = new TreeMap<>();
    
    /**
     * Obtains a number used to identify the {@linkplain Viewport} whose {@linkplain Camera} is positioned closest to some 
     * {@linkplain SoundSource}. 
     * 
     * @param soundSourcePosition a {@linkplain Vector3f} object used to represent the position of the sound source in 3D space
     * @return the ID number of the viewport with the closest camera relative to the sound source position
     */
    private static int findClosestViewport(Vector3f soundSourcePosition) {
        for(int i = 0; i < cameraDirections.size(); i++) {
            if(soundSourcePosition != null) distances.put(i, Math.sqrt(soundSourcePosition.distance(cameraPositions.get(i))));
            else distances.put(i, 0.0);
        }
        
        return distances.entrySet().stream()
                        .min(Comparator.comparingDouble(Map.Entry::getValue))
                        .get()
                        .getKey();
    }
    
    /**
     * Caches the current camera position and direction of every active viewport. This data is updated each frame and is used by
     * the audio system to dynamically reposition sound sources.
     * 
     * @param viewportID a number used to identify the viewport
     * @param camera the current {@linkplain Camera} object bound to the viewport
     */
    static void captureViewportCameraData(int viewportID, Camera camera) {
        cameraPositions.put(viewportID, camera.position);
        cameraDirections.put(viewportID, camera.direction);
    }
    
    /**
     * Called automatically by the engine during startup to initialize the audio system. During initialization, the engine will 
     * attempt to find any available audio devices and provide them to the user.
     * 
     * @param debugModeEnabled if true, additional developer tools will be available for use
     */
    static void init(boolean debugModeEnabled) {        
        for(int i = 0; i < MAX_SOURCES; i++) sourcePool[i] = new SoundSource(i);
        
        String primarySpeakerName = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
        List<String> deviceList   = null;
        
        //Attempt to populate the device list using OpenAL's enumeration extension
        if(alcIsExtensionPresent(NULL, "ALC_ENUMERATION_ALL_EXT") || alcIsExtensionPresent(NULL, "ALC_ENUMERATION_EXT")) {
            try { Thread.sleep(50); } catch(InterruptedException ignored) {}
            deviceList = getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
        }
        
        //Fallback in case OpenAL is unable to populate the device list
        if(deviceList == null || deviceList.isEmpty()) {
            Logger.logWarning("OpenAL failed to enumerate audio devices", null);
            deviceList = List.of(primarySpeakerName != null ? primarySpeakerName : "Primary Speaker");
        }
        
        //Populate list of available speaker objects
        for(String name : deviceList) {
            Speaker device = new Speaker(speakers.size(), name);
            if(device.open(debugModeEnabled)) speakers.put(device.index, device);
        }
        
        //Last ditch effort if OpenAL is being stubborn
        if(speakers.isEmpty()) {
            Speaker fallback = new Speaker(0, primarySpeakerName);
            if(fallback.open(debugModeEnabled)) speakers.put(0, fallback);
            Logger.logWarning("Could not find any available audio devices, " + 
                              "attempting to use fallback \"" + fallback.name + "\"", null);
        }
        
        try {
            setSpeaker(speakers.get(0));
        } catch(NullPointerException exception) {
            Logger.logError("Failed to find an available audio output device, " + 
                            "please check audio drivers or connected speakers.", exception);
        }
    }
    
    /**
     * Called once per frame to dynamically reposition sound sources using the camera orientation of every active viewport.
     */
    static void update() {
        for(int i = 0; i < speaker.getSoundSourceLimit(); i++) {
            if(sourcePool[i].getState() == AL_PLAYING) {
                int id = findClosestViewport(sourcePool[i].position);
                sourcePool[i].updatePosition(cameraPositions.get(id), cameraDirections.get(id));
            }
            
            sourcePool[i].updateQueue();
        }
        
        cameraPositions.clear();
        cameraDirections.clear();
    }
    
    /**
     * Frees all resources used by the audio system and shuts it down gracefully.
     */
    static void freeResources() {
        for(SoundSource source : sourcePool) {
            source.stop();
            source.delete();
        }
        
        sounds.values().forEach(Sound::delete);
        alcMakeContextCurrent(NULL);
        speakers.values().forEach(Speaker::close);
    }
    
    static Sound getSound(String name) {
        return (sounds.containsKey(name)) ? sounds.get(name) : Sound.FALLBACK;
    }
    
    public static void loadSound(String name, String filepath, String filename) {
        sounds.put(name, new Sound(filename, false));
    }
    
    public static void deleteSound(String name) {
        sounds.get(name).delete(); //TODO: check if this is still in use before deleting
        sounds.remove(name);
    }
    
    public static void pauseAll() {
        for(int i = 0; i < speaker.getSoundSourceLimit(); i++) {
            if(sourcePool[i].getState() == AL_PLAYING) {
                sourcePool[i].pause();
                sourcePool[i].pausedInBulk = true;
            } else {
                sourcePool[i].pausedInBulk = false;
            }
        }
    }
    
    public static void resumeAll() {
        for(int i = 0; i < speaker.getSoundSourceLimit(); i++) {
            if(sourcePool[i].pausedInBulk) sourcePool[i].play();
            sourcePool[i].pausedInBulk = false;
        }
    }
    
    public static boolean setSpeaker(Speaker speaker) {
        if(speaker == null) {
            Logger.logWarning("Failed to change current speaker. Parameter value cannot be null", null);
            return false;
        }
        
        if(Audio.speaker != null) {
            for(int i = 0; i < MAX_SOURCES; i++) sourcePool[i].cacheState();
        }
        
        var soundFiles = new HashMap<String, String>();
        
        sounds.forEach((name, sound) -> {
            soundFiles.put(name, sound.filename);
            sound.delete();
        });
        
        if(speaker.use()) {
            Audio.speaker = speaker;
            
            soundFiles.forEach((name, filename) -> sounds.put(name, new Sound(filename, false)));
            Sound.FALLBACK = new Sound("xjge_sound_fallback.ogg", true);
            
            for(int i = 0; i < Audio.speaker.getSoundSourceLimit(); i++) sourcePool[i].applyState();
            
            return true;
            
        } else {
            return false;
        }
    }
    
    public static float getSoundDuration(String name) {
        return (sounds.containsKey(name)) ? sounds.get(name).durationInSeconds : 0f;
    }
    
    public static int getSpeakerCount() {
        return speakers.size();
    }
    
    public static final Speaker getSpeaker() {
        return speaker;
    }
    
    public static final Speaker getSpeaker(int index) {
        return speakers.get(index);
    }
    
    /**
     * The underlying implementation doesn't provide device enumeration during 
     * runtime so all this does is expose the list of available audio devices 
     * found at startup
     * 
     * @return 
     */
    public static final NavigableMap<Integer, Speaker> findSpeakers() {
        return Collections.unmodifiableNavigableMap(speakers);
    }
    
    /**
     * Sources 0-63 are always guaranteed available since that's the imposed 
     * minimum set by the engine with 0-31 being reservable and 33-256 being 
     * transient
     * 
     * @param reserve
     * @return 
     */
    public static SoundSource findSoundSource(boolean reserve) {
        int limit = speaker.getSoundSourceLimit();
        
        if(reserve) {
            //Reserved slots: (0-31)
            for(int i = 0; i < MIN_SOURCES / 2; i++) {
                SoundSource candidate = sourcePool[i];
                int state = candidate.getState();

                if(state == AL_STOPPED || state == AL_INITIAL) {
                    Logger.logInfo("The sound source at index " + candidate.index +
                                   " has been reserved by the user");
                    candidate.reserved = true;
                    return candidate;
                }
            }
            
            Logger.logWarning("No reservable sound sources are available as the object pool " + 
                              "has been exhausted. The caller will need to resolve this.", 
                              null);
            return null;
            
        } else {
            //Transient slots: (32-MAX)
            for(int i = MIN_SOURCES / 2; i < limit; i++) {
                SoundSource candidate = sourcePool[i];
                int state = candidate.getState();

                if(state == AL_STOPPED || state == AL_INITIAL) {
                    return candidate;
                }
            }

            //If none are free we'll steal the source furthest from the listener
            float maxDistanceSquared = -1f;
            SoundSource farthest    = sourcePool[32];

            for(int i = MIN_SOURCES / 2; i < limit; i++) {
                SoundSource candidate = sourcePool[i];
                float distanceSquared  = candidate.position.distanceSquared(0f, 0f, 0f);

                if(distanceSquared > maxDistanceSquared) {
                    maxDistanceSquared = distanceSquared;
                    farthest = candidate;
                }
            }
            
            farthest.stop();
            return farthest;
        }
    }
    
}