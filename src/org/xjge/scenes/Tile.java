package org.xjge.scenes;

import org.joml.Vector3f;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class Tile {
    
    public int id;
    
    public Vector3f position;
    
    Tile(int id, Vector3f position) {
        this.id       = id;
        this.position = position;
    }
    
}