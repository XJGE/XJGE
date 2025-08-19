package org.xjge.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Audio2 {
    
    static final int MIN_SOURCES = 64;
    static final int MAX_SOURCES = 256;
    
    private static Speaker2 speaker;
    
    private static final SoundSource2[] sourcePool = new SoundSource2[MAX_SOURCES];
    
    private static final Map<String, Sound2> sounds = new HashMap<>();
    
    private static final Map<Integer, Vector3f> cameraPositions  = new HashMap<>();
    private static final Map<Integer, Vector3f> cameraDirections = new HashMap<>();
    private static final Map<Integer, Double> distances = new HashMap<>();
    
    private static final NavigableMap<Integer, Speaker2> speakers = new TreeMap<>();
    
    static void init() {        
        for(int i = 0; i < MAX_SOURCES; i++) sourcePool[i] = new SoundSource2(i);
        
        var deviceList = getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
        
        if(deviceList != null) {
            for(String name : deviceList) {
                Speaker2 device = new Speaker2(speakers.size(), name);
                if(device.open()) speakers.put(device.index, device);
            }
        } else {
            Logger.logWarning("Could not find any available audio output devices", null);
        }
        
        setSpeaker(speakers.get(0));
    }
    
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
    
    static void captureViewportCameraData(int viewportID, Camera camera) {
        cameraPositions.put(viewportID, camera.position);
        cameraDirections.put(viewportID, camera.direction);
    }
    
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
    
    static void cleanup() {
        alcMakeContextCurrent(NULL);
        speakers.values().forEach(device -> device.close());
    }
    
    static Sound2 getSound(String name) {
        return (sounds.containsKey(name)) ? sounds.get(name) : Sound2.placeholder;
    }
    
    public static void loadSound(String name, String filename) {
        sounds.put(name, new Sound2(XJGE.getAssetsFilepath(), filename));
    }
    
    public static void deleteSound(String name) {
        sounds.get(name).delete(); //TODO: check if this is still in use before deleting
        sounds.remove(name);
    }
    
    public static float getSoundDuration(String name) {
        return (sounds.containsKey(name)) ? sounds.get(name).durationInSeconds : 0f;
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
    
    public static boolean setSpeaker(Speaker2 speaker) {
        if(speaker == null) {
            Logger.logWarning("Failed to change current speaker. Parameter value cannot be null", null);
            return false;
        }
        
        if(Audio2.speaker != null) {
            for(int i = 0; i < MAX_SOURCES; i++) sourcePool[i].cacheState();
        }
        
        var soundFiles = new HashMap<String, String>();
        
        sounds.forEach((name, sound) -> {
            soundFiles.put(name, sound.filename);
            sound.delete();
        });
        
        var newSpeaker = new Speaker2(speaker.index, speaker.name);
        
        if(newSpeaker.open() && newSpeaker.use()) {
            if(Audio2.speaker != null) Audio2.speaker.close();
            Audio2.speaker = newSpeaker;
            speakers.put(newSpeaker.index, newSpeaker);
            
            soundFiles.forEach((name, filename) -> sounds.put(name, new Sound2(XJGE.getAssetsFilepath(), filename)));
            Sound2.placeholder = new Sound2("/org/xjge/assets/", "xjge_sound_beep.ogg");
            
            for(int i = 0; i < Audio2.speaker.getSoundSourceLimit(); i++) sourcePool[i].applyState();
            
            return true;
            
        } else {
            return false;
        }
    }
    
    public static int getNumSpeakers() {
        return speakers.size();
    }
    
    public static final Speaker2 getSpeaker() {
        return speaker;
    }
    
    public static final Speaker2 getSpeaker(int index) {
        return speakers.get(index);
    }
    
    /**
     * The underlying implementation doesn't provide device enumeration during 
     * runtime so all this does is expose the list of available audio devices 
     * found at startup
     * 
     * @return 
     */
    public static final NavigableMap<Integer, Speaker2> findSpeakers() {
        return Collections.unmodifiableNavigableMap(speakers);
    }
    
    public static SoundSource2 findSoundSource(boolean reserve) {
        
        //if(reserve) only pull from 0-63 since those are guaranteed, 64-256 are transient
        
        return sourcePool[0];
    }
    
}