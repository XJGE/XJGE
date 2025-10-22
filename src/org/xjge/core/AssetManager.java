package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class AssetManager {
    
    private static final List<AssetSource> sources = new ArrayList<>();
    
    static void addSource(AssetSource source) {
        AssetManager.sources.add(source);
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
    
}