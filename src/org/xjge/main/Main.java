package org.xjge.main;

//Created: Jan 28, 2023

import org.joml.Vector2i;
import org.xjge.core.Game;
import org.xjge.core.XJGE;


/**
 * @author J Hoffman
 * @since  
 */
public class Main {

    /*
    Entity textures are kept grayscale then color is applied using lookup table and seperate texture.    
    */
    
    public static void main(String args[]) {
        
        XJGE.init("/xjge/org/assets/", "org.xjge.main.", new Vector2i(384, 216));
        
        {
            
        }
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
}
