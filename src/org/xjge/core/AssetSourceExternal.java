package org.xjge.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents an external asset source rooted in a local filesystem directory.
 * <p>
 * Files are loaded using absolute paths relative to this root. In debug mode,
 * this class can monitor files for changes and notify listeners (e.g. textures,
 * shaders) to reload automatically.
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class AssetSourceExternal implements AssetSource {

    private final Path filepath;
    
    AssetSourceExternal(Path filepath) {
        this.filepath = filepath;
    }
    
    @Override
    public boolean exists(String filename) {
        return Files.exists(filepath.resolve(filename));
    }

    @Override
    public InputStream open(String filename) throws IOException {
        Path file = filepath.resolve(filename);
        if(!Files.exists(file)) throw new FileNotFoundException("Failed to locate external asset: \"" + file + "\"");
        return Files.newInputStream(file);
    }

}