package org.xjge.core;

import java.util.HashMap;
import static org.xjge.core.Input.*;

/**
 * Created: May 3, 2021
 * <p>
 * Puppet objects serve as a coupling between a {@link Control} from an 
 * {@link InputDevice} and an {@linkplain Command action} that can be performed 
 * by the player.
 * <p>
 * Any object that can be controlled by a player should utilize a puppet object.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Puppet {
    
    private boolean remove;
    
    public final String name;
    private InputDevice device;
    
    /**
     * A collection of commands this puppet will execute when a control is acted on.
     */
    public final HashMap<Control, Command> commands = new HashMap<>();
    
    /**
     * Creates a new puppet object. It's expected that implementing objects will 
     * populate the puppets {@link commands} collection inside of its constructor 
     * following the puppet objects initialization.
     * 
     * @param name the name used to identify this puppet internally by the engine
     */
    public Puppet(String name) {
        this.name = name;
    }
    
    /**
     * Executes the commands associated with this puppet object if an input 
     * device has been bound to it.
     * 
     * @param targetDelta a constant value denoting the desired time (in  seconds) 
     *                    it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
     */
    void processInput(double targetDelta, double trueDelta) {
        if(device != null && device.enabled && !commands.isEmpty()) {
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
    
    /**
     * Binds the specified input device to this puppet. While bound, the puppet 
     * will actively monitor state changes to the input devices controls and 
     * execute the commands that have been assigned to them. Passing 
     * {@link Input#NO_DEVICE NO_DEVICE} will unbind the current device from the
     * puppet.
     * 
     * @param deviceID the number used to identify the input device. One of: 
     * <table><caption></caption>
     * <tr><td>{@link Input#NO_DEVICE NO_DEVICE}</td>
     * <td>{@link Input#KEY_MOUSE_COMBO KEY_MOUSE_COMBO}</td></tr>
     * <tr><td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_1}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_2 GLFW_JOYSTICK_2}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_3 GLFW_JOYSTICK_3}</td>
     * <td>{@link org.lwjgl.glfw.GLFW#GLFW_JOYSTICK_4 GLFW_JOYSTICK_4}</td></tr>
     * <tr><td>{@link Input#AI_GAMEPAD_1 AI_GAMEPAD_1}</td>
     * <td>{@link Input#AI_GAMEPAD_2 AI_GAMEPAD_2}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_3}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_4}</td></tr>
     * <tr><td>{@link Input#AI_GAMEPAD_1 AI_GAMEPAD_5}</td>
     * <td>{@link Input#AI_GAMEPAD_2 AI_GAMEPAD_6}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_7}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_8}</td></tr>
     * <tr><td>{@link Input#AI_GAMEPAD_1 AI_GAMEPAD_9}</td>
     * <td>{@link Input#AI_GAMEPAD_2 AI_GAMEPAD_10}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_11}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_12}</td></tr>
     * <tr><td>{@link Input#AI_GAMEPAD_1 AI_GAMEPAD_13}</td>
     * <td>{@link Input#AI_GAMEPAD_2 AI_GAMEPAD_14}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_15}</td>
     * <td>{@link Input#AI_GAMEPAD_3 AI_GAMEPAD_16}</td></tr>
     * </table>
     */
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
                Logger.logWarning("Failed to set the input device of puppet \"" + name + 
                                  "\". Could not locate an input device at index " + deviceID, 
                                  null);
            }
        }
    }
    
    /**
     * Obtains the ID number of the input device currently bound to this puppet.
     * 
     * @return the number used to identify the input device
     */
    public int getInputDeviceID() {
        return (device == null) ? NO_DEVICE : device.id;
    }
    
}