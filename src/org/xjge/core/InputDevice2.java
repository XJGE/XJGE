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
    
    protected final String name;
    
    protected Map<String, Float> settings;
    protected Map<Control, Float> controlValues;
    protected Map<Control, Integer> controlBindings;
    
    InputDevice2(int id, String name) {
        this.id   = id;
        this.name = name;
    }
    
    protected abstract void captureControlState();
    
}