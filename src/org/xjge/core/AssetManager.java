package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
            if(source.exists(filename)) {
                return source.open(filename);
            }
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
        
        reloadRequests.add(() -> {
            reload(filename);
        });
    }
    
    static void processReloadRequests() {
        while(reloadRequests.peek() != null) reloadRequests.poll().run();
    }
    
    public static boolean exists(String filename) {
        return sources.stream().anyMatch(source -> source.exists(filename));
    }
    
    public static synchronized <T extends Asset> T load(String filename, Class<T> type) {
        if(assets.containsKey(filename)) return (T) assets.get(filename);
        
        try(InputStream stream = open(filename)) {
            T asset = type.getDeclaredConstructor(String.class).newInstance(filename);
            asset.load(stream);
            assets.put(filename, asset);
            
            return asset;
            
        } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | 
                NoSuchMethodException | SecurityException | InvocationTargetException | IOException exception) {
            Logger.logWarning("Failed to load asset using file: \"" + filename + "\" of type " + type.getSimpleName(), exception);
            return null;
        }
    }
    
    public static synchronized void reload(String filename) {
        try(InputStream stream = open(filename)) {
            assets.get(filename).reload(stream);
        } catch(IOException exception) {
            Logger.logWarning("Failed to reload asset using file: \"" + filename + "\"", exception);
        }
    }
    
    public static synchronized void release(String filename) {
        Asset asset = assets.remove(filename);
        if(asset != null) asset.release();
    }

    public static synchronized void clearAssets() {
        for(Asset asset : assets.values()) asset.release();
        assets.clear();
    }
    
}