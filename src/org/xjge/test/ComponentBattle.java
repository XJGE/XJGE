package org.xjge.test;

import org.xjge.core.Component;
import org.xjge.core.Control;
import org.xjge.core.Puppet;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentBattle extends Component {

    private Puppet puppet;
    
    ComponentBattle(ComponentUnit unit) {
        
        puppet = new Puppet("battle_" + unit.inputDeviceID);
        
        puppet.setInputDevice(unit.inputDeviceID);
        
    }
    
}