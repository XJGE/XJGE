package org.xjge.core;

import java.io.IOException;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public abstract class Asset {
    
    private boolean loaded;
    
    protected final String filename;
    
    protected Asset(String filename) {
        this.filename = filename;
    }

    public final void load() throws IOException {
        if(loaded) return;
        
        try {
            onLoad();
            loaded = true;
        } catch (IOException exception) {
            Logger.logError("Failed to load asset \"" + filename + "\"", exception);
            throw exception;
        }
    }
    
    public void reload() throws IOException {
        System.out.println(loaded);
        
        if(!loaded) return;
        
        try {
            onRelease();
            onLoad();
            onReload(); 
            Logger.logInfo("Reloaded asset: " + filename);
        } catch (IOException exception) {
            Logger.logError("Failed to reload asset \"" + filename + "\"", exception);
            throw exception;
        }
    }
    
    public final void release() {
        if (!loaded) return;
        try {
            onRelease();
        } catch (Exception exception) {
            Logger.logError("Error while unloading asset \"" + filename + "\"", exception);
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
    
    protected void onReload() {}
    
    protected abstract void onRelease();
    
}