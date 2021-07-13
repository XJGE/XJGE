package dev.theskidster.xjge2.core;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class Timer {
    
    public int time;
    public int speed;
    private final int initialTime;
    
    private boolean finished;
    private boolean start;
    
    private final Observable observable = new Observable(this);
    
    public Timer(int time, int speed) {
        this.time   = time;
        this.speed  = speed;
        initialTime = time;
    }
    
    public Timer(int time, int speed, PropertyChangeListener observer) {
        this.time   = time;
        this.speed  = speed;
        initialTime = time;
        
        observable.properties.put("finished", finished);
        observable.addObserver(observer);
    }
    
    public Timer(int time, int speed, List<PropertyChangeListener> observers) {
        this.time   = time;
        this.speed  = speed;
        initialTime = time;
        
        observable.properties.put("finished", finished);
        observers.forEach(observer -> observable.addObserver(observer));
    }
    
    public void start() { start = true; };
    
    public void stop()  { start = false; };
    
    public void reset() { time = initialTime; }
    
    public void update() {
        if(start) {
            if(time != 0) {
                if(Game.tick(speed)) time--;
            } else {
                finished = true;
                observable.notifyObservers("finished", finished);
            }
        }
    }
    
    public void restart() {
        finished = false;
        start    = true;
        
        observable.notifyObservers("finished", finished);
        reset();
    }
    
}