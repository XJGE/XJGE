package org.xjge.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class AssetSourceFallback implements AssetSource {

    private static final String ENGINE_ASSETS_FILEPATH = "org/xjge/assets/";
    private final ClassLoader classLoader;
    
    AssetSourceFallback(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public boolean exists(String filename) {
        return classLoader.getResource(ENGINE_ASSETS_FILEPATH + filename) != null;
    }

    @Override
    public InputStream load(String filename) throws IOException {
        InputStream stream = classLoader.getResourceAsStream(ENGINE_ASSETS_FILEPATH + filename);
        if(stream == null) throw new FileNotFoundException("Failed to locate engine asset: \"" + filename + "\"");
        return stream;
    }

}