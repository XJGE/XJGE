package org.xjge.core;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;

//Created: May 9, 2021

/**
 * An event the engine uses anytime a {@link Gamepad} is abruptly disconnected.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class EventGamepad extends Event {

    private final int jid;
    
    /**
     * Creates a new event which is called anytime a {@link Gamepad} is 
     * abruptly disconnected
     * 
     * @param jid a number used to identify which gamepad has been disconnected
     */
    EventGamepad(int jid) {
        super(0);
        this.jid = jid;
    }

    @Override
    public void resolve() {
        if(Input.missingGamepad != null) {
            resolved = Input.getDevicePresent(jid) && Input.missingGamepad.resolveEvent;
            
            if(resolved) {
                for(int i = GLFW_JOYSTICK_1; i < GLFW_JOYSTICK_5; i++) {
                    if(Input.getDevicePresent(i)) Input.revertEnabledState(i);
                }

                XJGE.removeUIWidget(jid, "discon " + jid);
            }
        } else {
            resolved = Input.getDevicePresent(jid);
        }
    }

}