package org.xjge.main;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Icon;
import org.xjge.graphics.Texture;

/**
 *
 * @author thesk
 */
public class OpacityWidget extends Widget {

    private final Icon icon;
    
    public OpacityWidget() {
        super(new Vector3i(), 0, 0);
        
        Texture texture = new Texture("spr_engineicons.png");
        
        icon = new Icon(texture, 64, 64);
        icon.setOpacity(1);
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
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

    @Override
    public void destroy() {
    }

}
