package org.xjge.test;

import org.xjge.core.Attribute;
import org.xjge.core.Control;
import org.xjge.core.Puppet;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class AttributeBattle extends Attribute {

    private Puppet puppet;
    
    AttributeBattle(AttributeUnit unit) {
        
        puppet = new Puppet("battle_" + unit.inputDeviceID);
        
        puppet.setInputDevice(unit.inputDeviceID);
        
    }
    
}