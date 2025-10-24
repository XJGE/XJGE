package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class AssetManager {
    
    private static final List<AssetSource> sources = new ArrayList<>();
    private static final Map<String, Asset> assets = new HashMap<>();
    
    static void addSource(AssetSource source) {
        sources.add(source);
    }
    
    public static boolean exists(String filename) {
        return sources.stream().anyMatch(source -> source.exists(filename));
    }
    
    public static InputStream open(String filename) throws IOException {
        for(AssetSource source : sources) {
            if(source.exists(filename)) {
                return source.open(filename);
            }
        }
        
        throw new IOException("Could not locate asset \"" + filename + "\" using any source");
    }
    
    public static synchronized <T extends Asset> T load(String filename, Class<T> type) {
        if(assets.containsKey(filename)) return (T) assets.get(filename);
        
        try {
            T asset = type.getDeclaredConstructor(String.class).newInstance(filename);
            asset.load();
            assets.put(filename, asset);
            
            return asset;
            
        } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | 
                NoSuchMethodException | SecurityException | InvocationTargetException | IOException exception) {
            Logger.logError("Failed to load asset \"" + filename + "\" of type " + type.getSimpleName(), exception);
            return null;
        }
    }
    
    static synchronized void reload(String filename) {
        Asset asset = assets.get(filename);
        
        if(asset != null) {
            try {
                asset.reload();
                Logger.logInfo("Reloaded asset: " + filename);
            } catch (IOException exception) {
                Logger.logError("Failed to reload asset \"" + filename + "\"", exception);
            }
        }
    }
    
    public static synchronized void release(String filename) {
        Asset asset = assets.remove(filename);
        
        if(asset != null) {
            try {
                asset.release();
                Logger.logInfo("Released asset: " + filename);
            } catch (Exception exception) {
                Logger.logError("Failed to release asset \"" + filename + "\"", exception);
            }
        }
    }

    public static synchronized void releaseAll() {
        for(Asset asset : assets.values()) {
            try {
                asset.release();
            } catch(Exception exception) {
                Logger.logError("Failed to release asset during cleanup", exception);
            }
        }
        
        assets.clear();
    }
    
}