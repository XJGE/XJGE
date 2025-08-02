package org.xjge.core;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Audio2 {

    private static Speaker2 speaker;
    
    private static final NavigableMap<Integer, Speaker> speakers = new TreeMap<>();
    
    void init() {
        
    }
    
    public boolean setSpeaker(Speaker2 speaker) {
        
    }
    
    public static int getNumSpeakers() {
        return speakers.size();
    }
    
    public static final Speaker2 getSpeaker() {
        return speaker;
    }
    
    public static final Speaker getSpeaker(int index) {
        return speakers.get(index);
    }
    
    public static final NavigableMap<Integer, Speaker2> findSpeakers() {
        
    }
    
}