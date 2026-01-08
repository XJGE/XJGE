package org.xjge.test;

import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class Main {

    public static void main(String[] args) {
        
        XJGE.init(true, "C:/Users/jhoff/Projects/XJGE/test", "org.xjge.test.");
        XJGE.setScene(new TestScene());
        XJGE.start();
        
    }
    
}