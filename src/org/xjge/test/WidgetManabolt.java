package org.xjge.test;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetManabolt extends Widget {

    private Texture targetTexture;
    private Icon targetIcon;
    
    WidgetManabolt() {
        targetTexture = new Texture("image_target.png");
        targetIcon = new Icon(targetTexture, 100, 100, true);
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        targetIcon.render();
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}