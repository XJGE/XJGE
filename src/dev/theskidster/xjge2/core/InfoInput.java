package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Icon;
import dev.theskidster.xjge2.graphics.Texture;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoInput {
    
    private Icon icon;
    
    InfoInput(Font font, Texture icons) {
        icon = new Icon(icons, 20, 20);
        icon.setPosition(40, 200, 0);
    }
    
    void render() {
        icon.render();
    }
    
}