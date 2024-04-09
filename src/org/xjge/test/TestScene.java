package org.xjge.test;

//Created: Apr 8, 2024

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    boolean texLoaded;
    
    public TestScene() {
        super("test");
        
        XJGE.addUIWidget(0, "test", new TestWidget());
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}
