package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.ui.Text;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoRuntime {

    
    private final Vector3f widgetPos = new Vector3f(4, 0, 0);
    private final Vector3f textPos   = new Vector3f();
    private final Text[] text        = new Text[5];
    
    InfoRuntime() {
        for(int i = 0; i < text.length; i++) {
            //text[i] = new Text(new Font()); //TODO; find a way to use existing font data
            //maybe by providing multiple glyph collections?
        }
    }
    
    void render() {
        //widgetPos.y = Window.getHeight() - Font.DEFAULT_SIZE;
        
        
    }
    
}