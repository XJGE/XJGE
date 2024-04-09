package org.xjge.test;

//Created: Apr 8, 2024

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    int count;
    
    boolean texLoaded;
    
    public TestScene() {
        super("test");
        
        XJGE.addUIWidget(0, "test", new TestWidget());
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        Logger.logInfo("test");
        
        if(Game.tick(60)) {
            count++;
        }
        
        if(count > 10) Logger.logSevere("Terminated", null);
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
