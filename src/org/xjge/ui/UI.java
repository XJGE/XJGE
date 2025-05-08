package org.xjge.ui;

import org.xjge.core.Logger;

/**
 * Created: May 8, 2025
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public class UI {

    private static UI instance;
    
    private UI() {
        //init shader
    }
    
    public static UI getInstance() {
        if(instance == null) {
            instance = new UI();
        }
        
        return instance;
    }
    
}