package org.xjge.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
abstract class InputDevice2 {

    protected static final int CONTROL_UNSUPPORTED = -1;
    
    boolean connected;
    protected boolean enabled = true;
    
    protected final int id;
    
    protected String name = "Unspecified Device";
    
    protected final Map<String, Float> settings           = new HashMap<>();
    protected final Map<Control, Float> controlValues     = new HashMap<>();
    protected final Map<Control, Integer> controlBindings = new HashMap<>();
    
    InputDevice2(int id) {
        this.id = id;
    }
    
    protected abstract void captureControlState();
    
}