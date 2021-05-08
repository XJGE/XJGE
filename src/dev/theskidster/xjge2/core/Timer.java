package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class Timer {

    public static boolean tick(int cycles) {
        return XJGE.tickCount % cycles == 0;
    }
    
}