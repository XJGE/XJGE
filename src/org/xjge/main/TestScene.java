package org.xjge.main;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Control;
import org.xjge.core.Input;
import static org.xjge.core.Input.AI_GAMEPAD_1;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * Apr 12, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    boolean cycle;
    
    TestPuppet testPuppet = new TestPuppet();
    
    public TestScene(String name) {
        super(name);
        
        Input.setDevicePuppet(AI_GAMEPAD_1, testPuppet.puppet);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.CROSS, (cycle) ? 1 : 0);
        cycle = !cycle;
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
