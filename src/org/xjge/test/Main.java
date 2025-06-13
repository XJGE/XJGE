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
        
        //Window2.setMinimumSize(640, 480);
        //Window2.setSize(640, 480);
        //Window2.setResizable(false);
        //Window2.setTitle("Test");
        //Window2.setIcon("icon.png");
        
        XJGE.setScene(new TestScene());
        
        XJGE.start(); //show window
        
    }
    
}