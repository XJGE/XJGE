package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import org.xjge.core.Camera;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class UIChangeCamera extends Widget {

    private boolean usingNext;
    private boolean firstPress;
    
    private final Camera previous;
    private final Camera next;
    
    UIChangeCamera(Camera previous, Camera next) {
        this.previous = previous;
        this.next = next;
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render() {
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
        if(key == GLFW_KEY_SPACE) {
            if(action == GLFW_PRESS) {
                if(!firstPress) {
                    usingNext = !usingNext;
                    
                    if(usingNext) {
                        Window.setViewportCamera(GLFW_JOYSTICK_1, new CameraTransition(previous, next, 0.5f));
                    } else {
                        Window.setViewportCamera(GLFW_JOYSTICK_1, new CameraTransition(next, previous, 0.5f));
                    }
                    
                    firstPress = true;
                }
            } else {
                firstPress = false;
            }
        }
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}