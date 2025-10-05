package org.xjge.core;

import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since 
 */
abstract class InputDevice2 {

    protected boolean enabled;
    
    protected final int id;
    
    protected Map<String, Float> settings;
    protected Map<Control, Float> inputValues;
    protected Map<Control, Integer> controlBindings;
    
    InputDevice2(int id) {
        this.id = id;
    }
    
    protected abstract void poll();
    
}