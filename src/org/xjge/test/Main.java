package org.xjge.test;

import org.xjge.core.Window;
import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Main {

    public static void main(String[] args) {
        
        XJGE.init(true, "C:/Users/jhoff/Projects/XJGE/art", "org.xjge.test.");
        //Window.setResolution(384, 216);
        XJGE.setScene(new SceneAdd());
        XJGE.enableBloom = true;
        //XJGE.setBloomThreshold(1000f);
        XJGE.start();
        
    }
    
}