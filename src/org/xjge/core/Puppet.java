package org.xjge.core;

import java.util.HashMap;

//Created: May 3, 2021

/**
 * Component object that enables implementing objects to make use of input 
 * actions captured from an input device by coupling 
 * {@linkplain Control interactive components} to some 
 * {@linkplain Command meaningful action}.
 * <p>
 * Any object that can be controlled by a player should utilize a puppet 
 * object.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see Input#setDevicePuppet(int, Puppet) 
 * @see Input#bindPreviousPuppet(int)
 */
public final class Puppet {

    public final Object object;
    
    /**
     * A collection of command objects the puppet will use. Command definitions 
     * should exhibit the following structure:
     * <blockquote><pre>
     * puppet.commands.put(CROSS,    myCommand1());
     * puppet.commands.put(TRIANGLE, myCommand2());
     * puppet.commands.put(SQUARE,   myCommand3());
     * </pre></blockquote>
     */
    public final HashMap<Control, Command> commands = new HashMap<>();
    
    /**
     * Creates a new puppet object. It is expected that implementing objects 
     * will populate the puppets {@link commands} collection inside of its 
     * constructor following the puppet objects initialization.
     * 
     * @param object the implementing object to be controlled with this puppet
     */
    public Puppet(Object object) {
        this.object = object;
    }
    
}