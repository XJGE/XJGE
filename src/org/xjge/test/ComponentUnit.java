package org.xjge.test;

import java.util.UUID;
import org.xjge.core.Command;
import org.xjge.core.Component;
import org.xjge.core.Control;
import org.xjge.core.Puppet;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentUnit extends Component {

    final boolean isPlayer;
    
    int maxHealth = 10;
    int maxMana   = 10;
    int health    = maxHealth;
    int mana      = maxMana;
    final int inputDeviceID;
    
    private final Puppet puppet;
    
    //final UnitAction[] actions = new UnitAction[3];
    //final String[] items = new String[3];
    //final ArrayList<String> spells = new ArrayList<>();
    
    private class InputState extends Command {
        boolean buttonPressed;
        boolean buttonPressedOnce;
        float inputValue;

        @Override
        public void execute(double targetDelta, double trueDelta) {
            buttonPressed     = buttonPressed();
            buttonPressedOnce = buttonPressedOnce();
            inputValue        = getInputValue();
            //TODO: extend this to capture more state depending on what's needed
        }
    }
    
    ComponentUnit(boolean isPlayer, int inputDeviceID, UUID uuid) {
        this.isPlayer      = isPlayer;
        this.inputDeviceID = inputDeviceID;
        
        puppet = new Puppet("unit_" + uuid); //TODO: name must be unique, added uuid
        puppet.setInputDevice(inputDeviceID);
        
        puppet.commands.put(Control.DPAD_UP,    new InputState());
        puppet.commands.put(Control.DPAD_DOWN,  new InputState());
        puppet.commands.put(Control.DPAD_LEFT,  new InputState());
        puppet.commands.put(Control.DPAD_RIGHT, new InputState());
        puppet.commands.put(Control.CROSS,      new InputState());
        puppet.commands.put(Control.CIRCLE,     new InputState());
        puppet.commands.put(Control.TRIANGLE,   new InputState());
        puppet.commands.put(Control.SQUARE,     new InputState());
        puppet.commands.put(Control.L1,         new InputState());
        puppet.commands.put(Control.R1,         new InputState());
    }
    
    boolean buttonPressed(Control control) {
        return ((InputState) puppet.commands.get(control)).buttonPressed;
    }
    
    boolean buttonPressedOnce(Control control) {
        return ((InputState) puppet.commands.get(control)).buttonPressedOnce;
    }
    
    float getInputValue(Control control) {
        return ((InputState) puppet.commands.get(control)).inputValue;
    }
    
}