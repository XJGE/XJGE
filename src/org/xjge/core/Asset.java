package org.xjge.core;

import java.io.IOException;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public abstract class Asset {
    
    private boolean loaded;
    
    private final String filename;
    
    protected Asset(String filename) {
        this.filename = filename;
    }

    final void load() throws IOException {
        if(loaded) return;
        onLoad();
        loaded = true;
    }
    
    final void reload() throws IOException {
        if(!loaded) return;
        onRelease();
        onLoad();
        onReload();
    }
    
    final void release() {
        if(!loaded) return;
        
        try {
            onRelease();
        } catch(Exception exception) {
            Logger.logError("Error encountered while releasing asset: \"" + filename + "\"", exception);
        } finally {
            loaded = false;
        }
    }
    
    public final boolean isLoaded() {
        return loaded;
    }
    
    public final String getFilename() {
        return filename;
    }
    
    protected abstract void onLoad() throws IOException;
    
    protected abstract void onReload() throws IOException;
    
    protected abstract void onRelease() throws IOException;
    
}