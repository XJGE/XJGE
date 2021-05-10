package dev.theskidster.xjge2.core;

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
            //TODO: reset input device states
        }
    }

}