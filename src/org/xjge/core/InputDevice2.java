package org.xjge.core;

import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since 
 */
abstract class InputDevice2 {

    boolean connected;
    protected boolean enabled = true;
    
    protected final int id;
    
    protected String name = "Unspecified Device";
    
    protected Map<String, Float> settings;
    protected Map<Control, Float> controlValues;
    protected Map<Control, Integer> controlBindings;
    
    InputDevice2(int id) {
        this.id = id;
    }
    
    protected abstract void captureControlState();
    
}