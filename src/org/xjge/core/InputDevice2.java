package org.xjge.core;

import org.xjge.components.Component;
import java.util.EnumMap;
import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since 
 */
final class InputDevice2 {

    boolean enabled = true;
    
    final int id;
    
    final String name;
    
    private final Map<Control, Component> controls = new EnumMap<>(Control.class);
    
    InputDevice2(int id, String name) {
        this.id   = id;
        this.name = name;
    }
    
    void bindControl(Control control, Component component) {
        controls.put(control, component);
    }
    
    //TODO: export controls?
    
    void poll(Puppet puppet) {
        for(var entry : controls.entrySet()) {
            var control   = entry.getKey();
            var component = entry.getValue();
            var command   = puppet.commands.get(control);
            
            if(command != null) {
                //command.execute(component.getInputValue(), control, this);
            }
        }
    }
    
    
    /*
    TODO:
    
    var controls = new HashMap<Control, Component>() {{
        put(Control.DPAD_UP, new ButtonComponent(GLFW_GAMEPAD_BUTTON_DPAD_UP));
        //etc.
    }};
    
    Input.setDeviceControls(GLFW_JOYSTICK_1, controls);
    
    //or if you wanna display the current controls to the user or whatever:
    
    var currentControls = Input.getDeviceControls(GLKW_JOYSTICK_1);
    
    */
    
}