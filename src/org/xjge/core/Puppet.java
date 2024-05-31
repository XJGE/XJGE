package org.xjge.core;

import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_2;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_3;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_4;
import static org.lwjgl.glfw.GLFW.glfwJoystickPresent;
import static org.xjge.core.Input.*;

//Created: May 3, 2021

/**
 * Component object that enables implementing objects to make use of input 
 * actions captured from an input device by coupling 
 * {@linkplain Control interactive components} to some 
 * {@linkplain Command meaningful action}.
 * <p>
 * Any object that can be controlled by a player should utilize a puppet 
 * object.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see Input#setDevicePuppet(int, Puppet) 
 * @see Input#bindPreviousPuppet(int)
 */
public final class Puppet {
    
    private boolean remove;
    
    public final String name;
    private InputDevice device;
    
    private final HashMap<Control, Command> commands;
    
    /**
     * Creates a new puppet object.It is expected that implementing objects 
     * will populate the puppets {@link commands} collection inside of its 
     * constructor following the puppet objects initialization.
     * 
     * @param name the name used to identify this puppet internally by the engine
     * @param commands a collection of commands this puppet will execute
     */
    public Puppet(String name, HashMap<Control, Command> commands) {
        this.name     = name;
        this.commands = commands;
    }
    
    public void setInputDevice(int deviceID) {
        if(deviceID == NO_DEVICE) {
            remove = true;
            device = null;
        } else {
            if(Input.getDevicePresent(deviceID)) {
                remove = false;
                device = Input.getDevice(deviceID);
                Input.addPuppet(this);
            } else {
                Logger.setDomain("input");
                Logger.logWarning("Failed to set the input device of puppet \"" + name + 
                                  "\". Could not locate an input device at index " + deviceID, 
                                  null);
                Logger.setDomain(null);
            }
        }
    }
    
    /**
     * Executes the commands associated with this puppet object if an input 
     * device has been bound to it.
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta   the actual time (in seconds) it took the current game
     *                    tick to complete
     */
    void processInput(double targetDelta, double trueDelta) {
        if(device != null && glfwJoystickPresent(device.id) && device.enabled) {
            commands.forEach((control, command) -> {
                device.poll(targetDelta, trueDelta, this, control, command);
            });
        }
    }
    
    /**
     * Determines if the puppet has requested removal. Once removed, a puppet 
     * cannot be controlled by an input device until {@link setInputDevice(int)}
     * is called again.
     * 
     * @return if true, the puppet will be removed from the engines internally 
     *         maintained list of controllable puppets
     */
    boolean removalRequested() {
        return remove;
    }
    
}