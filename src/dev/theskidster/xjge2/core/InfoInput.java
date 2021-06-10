package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Icon;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoInput {
    
    private final int PADDING = 8;
    
    private final int rectWidth;
    private final int rectHeight;
    
    private final Font font;
    private final Rectangle rectangle;
    private final Text text                = new Text();
    private final RectangleBatch rectBatch = new RectangleBatch(1);
    
    private final Icon[] icons       = new Icon[4];
    private final Vector3f[] textPos = new Vector3f[9];
    
    InfoInput(Font font, Texture iconTexture) {
        this.font = font;
        
        rectWidth  = 0;
        rectHeight = 0;
        rectangle  = new Rectangle();
        
        for(int i = 0; i < icons.length; i++) {
            icons[i] = new Icon(iconTexture, 64, 64);
            
        }
        
        
    }
    
    void updatePosition() {
        
    }
    
    void render() {
        
    }
    
    void freeBuffers() {
        
    }
    
}