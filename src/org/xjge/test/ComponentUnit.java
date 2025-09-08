package org.xjge.test;

import java.util.ArrayList;
import org.xjge.core.Component;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentUnit extends Component {

    int maxHealth;
    int maxMana;
    int health;
    int mana;
    final int inputDeviceID;
    
    final UnitAction[] actions = new UnitAction[3];
    
    final String[] items = new String[3];
    
    final ArrayList<String> spells = new ArrayList<>();
    
    ComponentUnit(int inputDeviceID) {
        this.inputDeviceID = inputDeviceID;
    }
    
    boolean turnFinished(Scene3D scene) {
        return false; //TODO: when this returns true this units turn is over and the next will be queued
    }
    
}