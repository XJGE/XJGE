package org.xjge.test;

import java.util.HashMap;
import org.xjge.core.Game;
import org.xjge.core.XJGE;
import org.xjge.scenes.TestScene;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    public static void main(String args[]) {
        
        /*
        TODO:
        - new init methods.
        - add bloom effect.
        - clean up codebase/remove remaining todos.
        */
        
        /*
        new init() methods!
        
        QUICK: uses default settings
            init(String filepath, String filepath);
        
        FANCY: specify your own through a list of arguments
            init(String filepath, String filepath, HashMap<String, Object> engineArgs);
        
        */
        
        XJGE.init("/org/xjge/assets/", "org.xjge.scenes.", true, null, false, false, false);
        
        var engineArgs = new HashMap<String, Object>();
        
        //argumentName : defaultValue
        engineArgs.put("resolution",          null);  //null or Vector2i
        engineArgs.put("resizableWindow",     false); //true or false
        engineArgs.put("retainFullscreen",    false); //true or false
        engineArgs.put("genGLInfoLog",        false); //true or false
        engineArgs.put("debugEnabled",        false); //true or false
        engineArgs.put("restrict4K",          true);  //true or false
        //engineArgs.put("optimizeSplitScreen", null);  //null or Vector2i (below 960x540)
        
        Game.setScene(new TestScene());
        
        XJGE.start();
    }
    
}