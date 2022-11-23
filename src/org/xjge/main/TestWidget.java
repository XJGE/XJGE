package org.xjge.main;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Icon;
import org.xjge.graphics.Texture;

/**
 *
 * @author thesk
 */
public class TestWidget extends Widget {

    private final Icon icon;
    private float angle = 45f;
    
    TestWidget() {
        super(new Vector3i(), Window.getMonitor().getWidth(), Window.getMonitor().getHeight());
        
        Texture texture = new Texture("spr_engineicons.png");
        icon = new Icon(texture, 64, 64, true);
    }
    
    @Override
    public void update() {
        //System.out.println(angle);
        
        angle = (angle > 360) ? (angle = 0) : (angle += 0.1f);
        //icon.setPosition(400, 400);
        //icon.setPosition(400, 400);
        //icon.setRotation(angle);
        
        //icon.modelMatrix.translation(400, 400, 0);
        //icon.modelMatrix.rotateZ(angle);
        
        icon.getModelMatrix().translation(400, 400, 0);
        icon.getModelMatrix().rotateY(angle);
        
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
