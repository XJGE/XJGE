package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.core.Command;
import java.util.HashMap;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public final class Puppet {

    public final Object object;
    
    public final HashMap<Control, Command> commands = new HashMap<>();
    
    public Puppet(Object object) {
        this.object = object;
    }
    
}