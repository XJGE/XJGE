package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Input;
import static org.xjge.core.Input.AI_GAMEPAD_1;
import org.xjge.core.UI;
import static org.xjge.test.GridSpaceStatus.NO_STATUS;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ActionSelect extends Action {

    private boolean optionSelected;
    private boolean alternate;
    
    private WidgetSelect ui;
    
    @Override
    boolean perform(Scene3D scene, ComponentUnit activeUnit, Map<UUID, ComponentUnit> units, Map<Vector3i, GridSpace> spaces) {
        if(ui == null) {
            ui = new WidgetSelect(activeUnit, units);
            UI.addWidget(GLFW_JOYSTICK_1, "action_select", ui);
            scene.camera.moveTo(new Vector3f(activeUnit.position.x, 10, activeUnit.position.z + 8), 0.003f);
            spaces.values().forEach(space -> space.status = NO_STATUS);
            
            if(UI.containsWidget(GLFW_JOYSTICK_1, "roll_outcome")) {
                UI.removeWidget(GLFW_JOYSTICK_1, "roll_outcome");
            }
        }
        
        /*
        TODO: we're gonna put the AI decision trees in the actions for now, if 
        this becomes unruly or we need more defined behavior per enemy we'll 
        probably need to use a component-style solution
        */
        if(activeUnit.name.equals("enemy")) {
            alternate = !alternate;
            Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.CROSS, (alternate) ? 0 : 1f);
        }
        
        if(ui != null) {
            if(!optionSelected) {
                if(activeUnit.buttonPressedOnce(Control.DPAD_UP))   ui.choice--;
                if(activeUnit.buttonPressedOnce(Control.DPAD_DOWN)) ui.choice++;
                if(activeUnit.buttonPressedOnce(Control.CROSS))     optionSelected = true;
            } else {
                activeUnit.action = switch(ui.getSelectedOption()) {
                    default -> new ActionMove();
                };
                
                //TODO: remove widget, move camera, etc.
            }
        }
        
        return false;
    }

}