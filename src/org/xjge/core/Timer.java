package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created: May 7, 2021
 * <p>
 * A simple monotonic timing mechanism. Useful for game events and other 
 * systems that require specialized timing intervals.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Timer {
    
    private final int time;
    private final int speed;
    
    private boolean finished;
    private boolean start;
    
    private final StopWatch stopWatch   = new StopWatch();
    private final Observable observable = new Observable(this);
    
    /**
     * Creates a new timer object that will step forward every time the 
     * specified number of update cycles have passed.
     * 
     * @param time the total number of intervals the timer must complete before 
     *             it is finished
     * @param speed the number of game ticks to wait before stepping forward. A 
     *              single tick typically takes 16 milliseconds.
     * 
     * @see Game#tick(int)
     */
    public Timer(int time, int speed) {
        this.time  = time;
        this.speed = speed;
        
        stopWatch.currTime = time;
    }
    
    /**
     * Creates a new timer object that will step forward every time the 
     * specified number of update cycles have passed. This constructor will 
     * notify an observer once the timer has finished.
     * 
     * @param time the total number of steps the timer must complete before it 
     *             is finished
     * @param speed the number of update ticks to wait before stepping forward. 
     *              A single tick typically takes a millisecond.
     * @param observer the object waiting for this timer to finish
     * 
     * @see Observable
     * @see Game#tick(int)
     */
    public Timer(int time, int speed, PropertyChangeListener observer) {
        this.time   = time;
        this.speed  = speed;
        
        stopWatch.currTime = time;
        observable.properties.put("finished", finished);
        observable.addObserver(observer);
    }
    
    /**
     * Creates a new timer object that will step forward every time the 
     * specified number of update cycles have passed. This constructor will 
     * notify multiple observers once the timer has finished.
     * 
     * @param time the total number of steps the timer must complete before it 
     *             is finished
     * @param speed the number of update ticks to wait before stepping forward. 
     *              A single tick typically takes a millisecond.
     * @param observers the objects waiting for this timer to finish
     * 
     * @see Observable
     * @see Game#tick(int)
     */
    public Timer(int time, int speed, List<PropertyChangeListener> observers) {
        this.time   = time;
        this.speed  = speed;
        
        stopWatch.currTime = time;
        observable.properties.put("finished", finished);
        observers.forEach(observer -> observable.addObserver(observer));
    }
    
    /**
     * Starts the timer.
     */
    public void start() { start = true; }
    
    /**
     * Stops the timer. Doing so will pause it at its current time.
     */
    public void stop() { start = false; }
    
    /**
     * Resets the time of the timer to its initial duration.
     */
    public void reset() { stopWatch.currTime = time; }
    
    /**
     * Updates the timer. It is expected that implementing objects using a 
     * timer component will supply some sort of {@code update()} method as part 
     * of a larger logic loop through which this can be called.
     */
    public void update() {
        if(start) {
            if(stopWatch.currTime != 0 && !finished) {
                stopWatch.tick(time, speed, false);
            } else {
                finished = true;
                observable.notifyObservers("finished", finished);
            }
        }
    }
    
    /**
     * Sets the timer back to its initial state and starts ticking again. Not 
     * to be confused with {@link reset()} which will only effect the timers 
     * time value. Restarting a timer will notify its observers once it has 
     * finished even if it had finished previously.
     */
    public void restart() {
        finished = false;
        start    = true;
        
        observable.notifyObservers("finished", finished);
        reset();
    }
    
    /**
     * Obtains the current value of the time field.
     * 
     * @return the current time the timer is at
     */
    public int getTime() {
        return stopWatch.currTime;
    }
    
}