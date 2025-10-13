package org.xjge.test;

import java.util.ArrayList;
import java.util.UUID;
import org.xjge.core.EntityComponent;
import org.xjge.core.Control;
import org.xjge.core.ControlState;
import org.xjge.core.Controllable;
import org.xjge.core.ControllableAction;
import org.xjge.core.Input;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentUnit extends EntityComponent {

    final boolean isPlayer;
    
    int moveRange = 5;
    
    int maxHealth = 10;
    int maxMana   = 10;
    int health    = maxHealth;
    int mana      = maxMana;
    final int inputDeviceID;
    
    private final Controllable[] controllables = new Controllable[2];
    
    //TODO: these could be their own components like ComponentInventory and ComponentSpellbook
    final ArrayList<String> items = new ArrayList<>();
    final ArrayList<Spell> spells = new ArrayList<>();
    
    private class InputState extends ControllableAction {
        boolean axisMoved;
        boolean buttonPressed;
        boolean buttonPressedOnce;
        float inputValue;

        @Override
        public void perform(ControlState controlState, double targetDelta, double trueDelta) {
            axisMoved         = controlState.axisMoved();
            buttonPressed     = controlState.buttonPressed();
            buttonPressedOnce = controlState.buttonPressedOnce();
            inputValue        = controlState.getInputValue();
            //TODO: extend this to capture more state depending on what's needed
        }
    }
    
    ComponentUnit(boolean isPlayer, int inputDeviceID, UUID uuid) {
        this.isPlayer      = isPlayer;
        this.inputDeviceID = inputDeviceID;
        
        items.add("Health Potion");
        //items.add("Mana Potion");
        //items.add("Speed Potion");
        
        /*
        TODO: Just providing a standard set of spells here for now, in the future
        these will be learnable and/or unique to characters.
        */
        spells.add(new Spell(3, "Flash"));
        spells.add(new Spell(5, "Manabolt"));
        spells.add(new Spell(7, "Mud Trap"));
        
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
        
        for(int i = 0; i < controllables.length; i++) {
            controllables[i] = new Controllable("unit_" + i);
            
            controllables[i].actions.put(Control.DPAD_UP,       new InputState());
            controllables[i].actions.put(Control.DPAD_DOWN,     new InputState());
            controllables[i].actions.put(Control.DPAD_LEFT,     new InputState());
            controllables[i].actions.put(Control.DPAD_RIGHT,    new InputState());
            controllables[i].actions.put(Control.CROSS,         new InputState());
            controllables[i].actions.put(Control.CIRCLE,        new InputState());
            controllables[i].actions.put(Control.TRIANGLE,      new InputState());
            controllables[i].actions.put(Control.SQUARE,        new InputState());
            controllables[i].actions.put(Control.L1,            new InputState());
            controllables[i].actions.put(Control.R1,            new InputState());
            controllables[i].actions.put(Control.RIGHT_STICK_X, new InputState());
            controllables[i].actions.put(Control.RIGHT_STICK_Y, new InputState());
            
            if(inputDeviceID == Input.KEYBOARD) {
                controllables[i].setInputDevice(i == 0 ? Input.KEYBOARD : Input.MOUSE);
            } else {
                controllables[i].setInputDevice(inputDeviceID);
            }
        }
    }
    
    boolean axisMoved(Control control, int index) {
        return ((InputState) controllables[index].actions.get(control)).axisMoved;
    }
    
    boolean buttonPressed(Control control, int index) {
        return ((InputState) controllables[index].actions.get(control)).buttonPressed;
    }
    
    boolean buttonPressedOnce(Control control, int index) {
        return ((InputState) controllables[index].actions.get(control)).buttonPressedOnce;
    }
    
    float getInputValue(Control control, int index) {
        return ((InputState) controllables[index].actions.get(control)).inputValue;
    }
    
}