package org.xjge.core;

import java.io.InputStream;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public abstract class Asset {
    
    private boolean loaded;
    protected boolean useFallback;
    
    private final String filename;
    
    protected Asset(String filename) {
        this.filename = filename;
    }

    final void load(InputStream stream) {
        if(loaded) return;
        onLoad(stream);
        loaded = true;
    }
    
    final void reload(InputStream stream) {
        if(!loaded) return;
        onRelease();
        onLoad(stream);
        onReload();
    }
    
    final void release() {
        if(!loaded) return;
        onRelease();
        loaded = false;
    }
    
    public final boolean isLoaded() {
        return loaded;
    }
    
    public final String getFilename() {
        return filename;
    }
    
    protected abstract void onLoad(InputStream file);
    
    protected abstract void onReload();
    
    protected abstract void onRelease();
    
    protected abstract <T extends Asset> T onLoadFailure();
    
}