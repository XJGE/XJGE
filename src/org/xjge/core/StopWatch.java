package org.xjge.core;

//Created: Feb 5, 2022

/**
 * @author J Hoffman
 * @since  2.0.0
 */
public class StopWatch {

    int startTick;
    int currTime;
    
    boolean startTickSet;
    
    public boolean tick(int time, int speed, boolean increment) {
        if(startTickSet) {
            if(increment) {
                if(currTime == ((time < 1) ? 1 : time)) {
                    startTickSet = false;
                    currTime     = 0;
                    return false;
                }
            } else {
                if(currTime == -1) {
                    startTickSet = false;
                    currTime     = time - 1;
                    return false;
                }
            }
        }
        
        if(!startTickSet) {
            startTick    = Game.getTickCount();
            startTickSet = true;
            if(!increment) currTime = time - 1;
            return true;
        }
        
        int diff = (startTick > Game.getTickCount()) 
                 ? (Game.getTickCount() + Game.TICKS_PER_HOUR) - startTick
                 : Game.getTickCount() - startTick;
        
        if(diff % speed == 0) {
            currTime = (increment) ? currTime + 1 : currTime - 1;
            return (increment) ? currTime != time : currTime != -1;
        }
        
        return false;
    }
    
    public int getTime() {
        return currTime;
    }
    
}
