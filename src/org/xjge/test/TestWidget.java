package org.xjge.test;

//Created: Jul 20, 2021

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Icon;
import org.xjge.graphics.Texture;


/**
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    Icon icon;
    
    public TestWidget(Vector3i position, int width, int height) {
        super(position, width, height);
        
        Texture texture = new Texture("img_buster.png");
        icon = new Icon(texture, 20, 20);
        icon.setPosition(0, 0);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        icon.render();
    }

    @Override
    public void setSplitPosition(Split split, int viewportWidth, int viewportHeight) {
        icon.setPosition(viewportWidth - 20, viewportHeight - 20);
    }

}