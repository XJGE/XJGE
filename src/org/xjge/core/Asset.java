package org.xjge.core;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public abstract class Asset {
    
    private boolean loaded;
    
    private final String filename;
    
    private final Set<AssetReloadListener> reloadListeners = new LinkedHashSet<>();
    
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
        
        Logger.logInfo(getClass().getSimpleName() + " file: " + "\"" + filename + "\" reloaded successfully");
        
        //Notify listeners in debug mode only, using a new set here to avoid concurrent modification
        if(XJGE.debugModeEnabled()) {
            for(var listener : new LinkedHashSet<>(reloadListeners)) listener.onAssetReload(this);
        }
    }
    
    final void release() {
        if(!loaded) return;
        onRelease();
        loaded = false;
    }
    
    public void addAssetListener(AssetReloadListener listener) {
        if(!XJGE.debugModeEnabled()) return;
        reloadListeners.add(listener);
    }
    
    public void removeAssetListener(AssetReloadListener listener) {
        reloadListeners.remove(listener);
    }
    
    public final boolean isLoaded() {
        return loaded;
    }
    
    public final String getFilename() {
        return filename;
    }
    
    protected abstract void onLoad(InputStream file);
    
    protected abstract void onRelease();
    
    protected abstract <T extends Asset> T onLoadFailure();
    
}