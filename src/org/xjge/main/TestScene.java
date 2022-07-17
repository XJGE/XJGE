package org.xjge.main;

import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Scene;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 *
 * @author thesk
 */
public class TestScene extends Scene {

    boolean flip;
    
    private String prevAspect;
    
    TestScene() {
        super("test");
        
        XJGE.addUIWidget(GLFW_JOYSTICK_1, "palette", new TestWidget());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(prevAspect != null && !prevAspect.equals(Window.getMonitor().getAspectRatio())) {
            if(Window.getMonitor().getAspectRatio().equals("4:3")) {
                XJGE.setResolution(320, 240);
            } else {
                XJGE.setResolution(384, 216);
            }
        }
        
        prevAspect = Window.getMonitor().getAspectRatio();
        
        /*
        if(Game.tick(30)) flip = !flip;
        XJGE.setResolution((flip) ? 640 : 320, 480);
        */
        
        /*
        if(Window.getMonitor().getAspectRatio().equals("4:3")) {
            XJGE.setResolution(320, 240);
        } else {
            XJGE.setResolution(384, 216);
        }*/
        
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
