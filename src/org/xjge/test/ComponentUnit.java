package org.xjge.test;

import java.util.ArrayList;
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
    
    //TODO: these could be their own components like ComponentInventory and ComponentSpellbook
    final String[] items = new String[3];
    final ArrayList<String> spells = new ArrayList<>();
    
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
        
        /*
        TODO: Just providing a standard set of spells here for now, in the future
        these will be learnable and/or unique to characters.
        */
        spells.add("Flash"); //Instantly teleport to an unoccupied gridspace of your choice
        spells.add("Manabolt"); //Simple ranged attack that requires a aiming RTR/minigame
        spells.add("Mud Trap"); //AOE spell that slows movement while they're inside an affected gridspace
        
        /*
        FLASH MINIGAME:
        - The player selects a group of grid spaces (lets limit it to 5x5 area for now).
        - The target space cycles rapidly (highlights moves across valid destinations)
        - players must press the action button at the right moment to select the desired 
          space or risk ending up somewhere else in the targeted area.
        
        MANABOLT MINIGAME:
        A target appears over the unit we select, players must keep the cursor
        within the target before the time limit expires. The cursor will move 
        and rotate on its own requiring the player to readjust it frequently.
        
        MUD TRAP MINIGAME:
        - The player selects a group of grid spaces (lets limit it to 5x5 area for now).
        - The camera takes an isometric birds-eye view of the selected area 
          (or we display a seperate grid mirroring the spaces?)
        - The player must "paint" each space using the cursor or analog stick 
          before the timer runs out.
        - brush size is roughly 1/4 that of a gridspace meaning the space must be
          sufficiently colored in for it to register as "selected"
        */
        
        /*
        ITEMS: (All of these use the crystal ball minigame to determine effectiveness)
        Health Potion - Restores HP 
        Mana Potion - Restores Mana
        Speed Potion - Increases range you can move for one turn
        */
        
        /*
        CRYSTAL BALL MINIGAME:
        - Mechanically works like rolling dice in mario party. (light RNG)
        - Crystal Balls can be unique to characters
        - A Crystal Ball appears on screen with a hand, players must wave a hand
          for numbers to start cycling using an analog stick
        - While moving the hand they'll press the action button to select a number
        */
        
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