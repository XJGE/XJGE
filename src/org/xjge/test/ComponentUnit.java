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
    
    final String[] items = new String[3];
    
    final ArrayList<String> spells = new ArrayList<>();
    
    ComponentUnit(int inputDeviceID) {
        this.inputDeviceID = inputDeviceID;
    }
    
    boolean turnFinished(Scene3D scene) {
        return false;
    }
    
}