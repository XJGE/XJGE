package org.xjge.core;

/**
 * Provides a simple timing mechanism that exhibits greater accuracy than 
 * {@link Game#tick(int)} alone by capturing the timestamp of the initial game 
 * tick from which {@link tick(int, int, boolean)} was called and comparing it 
 * to subsequent ticks until the specified number of cycles have passed.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Timer {

    int startTick;
    int currentTime;
    
    boolean startTickSet;
    
    /**
     * Simplified version of {@link tick(int, int, boolean)} that mimics the 
     * functionality of the {@link Game#tick(int)} method but with improved 
     * accuracy.
     * 
     * @param speed the number of game ticks to wait before changing the time 
     *              value. A single tick typically takes 16 milliseconds.
     * 
     * @return true every time the specified number of cycles has been reached
     */
    public boolean tick(int speed) {
        return tick(2, speed, false);
    }
    
    /**
     * Returns true anytime the specified number of update iterations (or 
     * cycles) have been reached following the current game tick this method 
     * was initially called at. 
     * <p>
     * The number of ticks (provided through 
     * {@link getTime()}) will reset upon reaching the value specified in the 
     * {@code time} argument. However, if {@code false} is passed to the 
     * {@code increment} argument, it will instead reset upon reaching zero.
     * <p>
     * NOTE: The value of {@code time} is zero-inclusive. As such, passing a 
     * value of five (counting down) will actually start at four, and reset 
     * after zero has been reached. This method will always return true on the 
     * first call.
     * 
     * @param time the total number of intervals the timer must complete before 
     *             it restarts
     * @param speed the number of game ticks to wait before changing the time 
     *              value. A single tick typically takes 16 milliseconds.
     * @param increment if true, the stopwatch will count upwards. Otherwise it 
     *                  will count down.
     * 
     * @return true every time the specified number of cycles has been reached
     */
    public boolean tick(int time, int speed, boolean increment) {
        if(startTickSet) {
            if(increment) {
                if(currentTime == ((time < 1) ? 1 : time)) {
                    startTickSet = false;
                    currentTime  = 0;
                    return false;
                }
            } else {
                if(currentTime == -1) {
                    startTickSet = false;
                    currentTime  = time - 1;
                    return false;
                }
            }
        }
        
        if(!startTickSet) {
            startTick    = XJGE.getTickCount();
            startTickSet = true;
            currentTime  = (!increment) ? time - 1 : 0;
            return true;
        }
        
        int diff = (startTick > XJGE.getTickCount()) 
                 ? (XJGE.getTickCount() + XJGE.TICKS_PER_HOUR) - startTick
                 : XJGE.getTickCount() - startTick;
        
        if(diff % speed == 0) {
            currentTime = (increment) ? currentTime + 1 : currentTime - 1;
            return (increment) ? currentTime != time : currentTime != -1;
        }
        
        return false;
    }
    
    /**
     * Obtains the current value of the time field.
     * 
     * @return the current time the stopwatch is at
     */
    public int getTime() {
        return currentTime;
    }
    
    /**
     * Resets the timer. The {@link getTime()} method will return its starting 
     * value following the next call to {@link tick(int, int, boolean)}.
     */
    public void reset() {
        startTickSet = false;
    }
    
}