package org.xjge.test;

import org.xjge.core.Window;
import org.xjge.core.XJGE;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class Main {
    
    public static void main(String args[]) {
        
        XJGE.init(true, "/org/xjge/assets/", "org.xjge.test."); //create window with default settings
        
        Window.addObserver(new WindowObserver());
        
        //Window.setMinimumSize(640, 480);
        //Window.setSize(640, 480);
        //Window.setResizable(false);
        //Window.setTitle("Test");
        //Window.setIcon("icon.png");
        //Window.setResolution(384, 216);
        
        XJGE.setScene(new TestScene());
        
        XJGE.start(); //show window
        
    }
    
}