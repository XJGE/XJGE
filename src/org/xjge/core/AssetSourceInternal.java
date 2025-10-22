package org.xjge.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class AssetSourceInternal implements AssetSource {

    private static final String ASSETS_PACKAGE = "assets/";
    private final String assetsFolderFilepath;
    private final ClassLoader classLoader;
    
    AssetSourceInternal(ClassLoader classLoader) {
        this(classLoader, ASSETS_PACKAGE);
    }
    
    AssetSourceInternal(ClassLoader classLoader, String assetsFolderFilepath) {
        this.classLoader = classLoader;
        this.assetsFolderFilepath = assetsFolderFilepath.endsWith("/") ? assetsFolderFilepath : assetsFolderFilepath + "/";
    }
    
    @Override
    public boolean exists(String filename) {
        return classLoader.getResource(assetsFolderFilepath + filename) != null;
    }

    @Override
    public InputStream open(String filename) throws IOException {
        InputStream stream = classLoader.getResourceAsStream(assetsFolderFilepath + filename);
        if(stream == null) throw new FileNotFoundException("Failed to locate internal asset: \"" + assetsFolderFilepath + filename + "\"");
        return stream;
    }

}