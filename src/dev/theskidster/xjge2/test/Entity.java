package dev.theskidster.xjge2.test;

import static dev.theskidster.xjge2.core.Control.*;
import dev.theskidster.xjge2.core.Puppet;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public class Entity {

    Puppet puppet = new Puppet(this);
    
    Entity() {
        puppet.commands.put(CROSS, new LogControlName("Cross"));
        puppet.commands.put(CIRCLE, new LogControlName("Circle"));
        puppet.commands.put(SQUARE, new LogControlName("Square"));
        puppet.commands.put(TRIANGLE, new LogControlName("Triangle"));
        puppet.commands.put(L1, new LogControlName("L1"));
        puppet.commands.put(R1, new LogControlName("R1"));
        puppet.commands.put(SHARE, new LogControlName("Share"));
        puppet.commands.put(OPTIONS, new LogControlName("Options"));
        puppet.commands.put(PS_BUTTON, new LogControlName("PS button"));
        puppet.commands.put(L3, new LogControlName("L3"));
        puppet.commands.put(R3, new LogControlName("R3"));
        puppet.commands.put(DPAD_UP, new LogControlName("D-Pad up"));
        puppet.commands.put(DPAD_DOWN, new LogControlName("D-Pad down"));
        puppet.commands.put(DPAD_LEFT, new LogControlName("D-Pad left"));
        puppet.commands.put(DPAD_RIGHT, new LogControlName("D-Pad right"));
        puppet.commands.put(RIGHT_STICK_X, new LogControlName("Right stick x"));
        puppet.commands.put(RIGHT_STICK_Y, new LogControlName("Right stick y"));
        puppet.commands.put(LEFT_STICK_X, new LogControlName("Left stick x"));
        puppet.commands.put(LEFT_STICK_Y, new LogControlName("Left stick y"));
        puppet.commands.put(L2, new LogControlName("L2"));
        puppet.commands.put(R2, new LogControlName("R2"));
    }
    
}