package dev.theskidster.xjge2.core;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

/**
 * @author J Hoffman
 * Created: May 9, 2021
 */

/**
 * An event the engine uses anytime a {@link Gamepad} is abruptly disconnected.
 */
final class EventGamepad extends Event {

    private final int jid;
    
    /**
     * Creates a new event which is called anytime a {@link Gamepad} is abruptly disconnected
     * 
     * @param jid a number used to identify which gamepad has been disconnected
     */
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