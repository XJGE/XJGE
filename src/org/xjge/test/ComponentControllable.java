package org.xjge.test;

import org.xjge.core.Component;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentControllable extends Component {

    final int inputDeviceID;
    
    ComponentControllable(int inputDeviceID) {
        this.inputDeviceID = inputDeviceID;
    }
    
}