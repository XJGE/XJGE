package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
interface AssetSource {

    boolean exists(String filename);
    
    InputStream load(String filename) throws IOException;
    
}