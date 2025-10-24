package org.xjge.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

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
final class AssetSourceExternal implements AssetSource, AutoCloseable {

    private volatile boolean watching = true;
    
    private final Path filepath;
    private final WatchService watchService;
    private final Thread watcherThread;
    
    private final List<Consumer<Path>> listeners = new CopyOnWriteArrayList<>();
    private final Map<WatchKey, Path> watchKeys  = new HashMap<>();
    
    AssetSourceExternal(Path filepath) throws IOException {
        this.filepath = filepath;
        
        if(!Files.isDirectory(this.filepath)) {
            throw new IllegalArgumentException("Assets folder must be an absolute filepath");
        }

        this.watchService = FileSystems.getDefault().newWatchService();
        registerAll(this.filepath);

        watcherThread = new Thread(this::processEvents, "AssetSourceExternal-Watcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }
    
    public void addChangeListener(Consumer<Path> listener) {
        listeners.add(listener);
    }
    
    public void removeChangeListener(Consumer<Path> listener) {
        listeners.remove(listener);
    }

    private void registerAll(Path start) throws IOException {
        Files.walk(start)
             .filter(Files::isDirectory)
             .forEach(this::register);
    }

    private void register(Path directory) {
        try {
            WatchKey key = directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchKeys.put(key, directory);
        } catch (IOException exception) {
            Logger.logWarning("Failed to register directory for file watcher: " + directory, exception);
        }
    }

    private void processEvents() {
        while(watching) {
            WatchKey key;
            try {
                key = watchService.take(); //Blocking
            } catch(InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }

            Path directory = watchKeys.get(key);
            if(directory == null) continue;

            for(WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if(kind == OVERFLOW) continue;

                Path name    = (Path) event.context();
                Path changed = directory.resolve(name);

                //Notify listeners asynchronously
                for(Consumer<Path> listener : listeners) {
                    try {
                        listener.accept(changed);
                    } catch(Exception exception) {
                        Logger.logError("Error notifying asset listener for " + changed, exception);
                    }
                }

                //Register newly created directories
                if(kind == ENTRY_CREATE && Files.isDirectory(changed)) {
                    register(changed);
                }
            }

            key.reset();
        }
    }
    
    @Override
    public boolean exists(String filename) {
        return Files.exists(filepath.resolve(filename));
    }

    @Override
    public InputStream load(String filename) throws IOException {
        Path file = filepath.resolve(filename);
        if(!Files.exists(file)) throw new FileNotFoundException("Failed to locate external asset: \"" + file + "\"");
        return Files.newInputStream(file);
    }

    @Override
    public void close() throws Exception {
        watching = false;
        watcherThread.interrupt();
        watchService.close();
    }
    
    public Path getFilepath() {
        return filepath;
    }

}