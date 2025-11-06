package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class AssetManager {
    
    private static final List<AssetSource> sources = new ArrayList<>();
    private static final Map<String, Asset> assets = new HashMap<>();
    
    private static final Queue<Runnable> reloadRequests = new ConcurrentLinkedQueue<>();
    
    private static InputStream open(String filename) throws IOException {
        for(AssetSource source : sources) {
            if(source.exists(filename)) return source.open(filename);
        }
        
        throw new IOException("Could not locate file: \"" + filename + "\" using any source");
    }
    
    /**
     * TODO: mention that the external source should point to the same /project/assets/ package that will be compiled into the jar
     * when the project is built, I don't feel like implementing a feature to automatically pull these in elsewhere
     * 
     * @param source 
     */
    static void addSource(AssetSource source) {
        sources.add(source);
    }
    
    static void queueReload(String filename) {
        Logger.logInfo("Change detected for asset file: \"" + filename + "\"");
        reloadRequests.add(() -> reload(filename));
    }
    
    static void processReloadRequests() {
        while(reloadRequests.peek() != null) reloadRequests.poll().run();
    }
    
    static void releaseSounds() {
        for(Asset asset : assets.values()) {
            if(asset instanceof Sound sound) sound.delete();
        }
    }
    
    static void reloadSounds() {
        var soundFiles = new ArrayList<String>();
        
        //Grab old sound filenames to prevent concurrent modification
        for(Asset asset : assets.values()) {
            if(asset instanceof Sound) soundFiles.add(asset.getFilename());
        }
        
        soundFiles.forEach(filename -> {
            Sound sound = new Sound(filename);
            
            try(InputStream stream = open(filename)) {
                sound.load(stream);
                assets.put(filename, sound);
            } catch(IllegalArgumentException | SecurityException | IOException exception) {
                Logger.logWarning("Failed to reload sound file: \"" + filename + "\" following speaker change", exception);
                assets.put(filename, Sound.FALLBACK);
            }
        });
    }
    
    static Sound getSound(String filename) {
        return (assets.containsKey(filename) && assets.get(filename) instanceof Sound sound) ? sound : null;
    }
    
    public static boolean exists(String filename) {
        return sources.stream().anyMatch(source -> source.exists(filename));
    }
    
    public static synchronized <T extends Asset> T load(String filename, Supplier<T> factory) {
        if(assets.containsKey(filename)) return (T) assets.get(filename);
        
        T asset = factory.get();
        
        try(InputStream stream = open(filename)) {
            asset.load(stream);
            assets.put(filename, asset);
            return asset;
        } catch(IllegalArgumentException | SecurityException | IOException exception) {
            Logger.logWarning("Failed to load asset", exception);
            return asset.onLoadFailure();
        }
    }
    
    public static synchronized boolean reload(String filename) {
        Asset asset = assets.get(filename);
        
        if(asset == null) {
            if(exists(filename)) {
                //TODO: load/register asset from new file, we run into this issue if the file doesn't exist during the first load call
            } else {
                Logger.logWarning("Failed to reload asset using file: \"" + filename + "\" no such asset exists", null);
            }
            
            return false;
        }
        
        try(InputStream stream = open(filename)) {
            assets.get(filename).reload(stream);
            asset.useFallback = false;
            return true;
        } catch(IOException exception) {
            Logger.logWarning("Failed to reload asset using file: \"" + filename + "\"", exception);
            asset.useFallback = true;
            return false;
        }
    }
    
    public static synchronized void release(String filename) {
        Asset asset = assets.remove(filename);
        if(asset != null) asset.release();
    }
    
    public static synchronized void releaseAll() {
        for(Asset asset : assets.values()) asset.release();
        assets.clear();
    }
    
}