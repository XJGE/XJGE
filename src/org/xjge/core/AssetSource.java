package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author J Hoffman
 * @since 
 */
interface AssetSource {

    boolean exists(String filepath);
    
    InputStream open(String filepath) throws IOException;
    
}