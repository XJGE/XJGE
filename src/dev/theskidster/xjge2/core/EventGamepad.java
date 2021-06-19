package dev.theskidster.xjge2.core;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

/**
 * @author J Hoffman
 * Created: May 9, 2021
 */

final class EventGamepad extends Event {

    private final int jid;
    
    EventGamepad(int jid) {
        super(0);
        this.jid = jid;
    }

    @Override
    public void resolve() {
        resolved = glfwJoystickPresent(jid);
        
        if(resolved) {
            for(int i = GLFW_JOYSTICK_1; i < GLFW_JOYSTICK_5; i++) {
                if(Input.getDevicePresent(i)) Input.revertEnabledState(i);
            }
            
            XJGE.removeUIWidget(jid, "discon " + jid);
        }
    }

}