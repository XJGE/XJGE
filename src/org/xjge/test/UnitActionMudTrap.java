package org.xjge.test;

import java.util.List;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMudTrap extends UnitAction {

    private int stage;
    
    private final List<GridSpace> area;
    
    UnitActionMudTrap(List<GridSpace> area) {
        this.area = area;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                /*
                Lets implement it like this for now:
                
                Player must paint spaces by selecting each grid space and clicking to
                change their color before the timer runs out- how dark a space is determines
                it's chance to be considered "muddy" once evaluated
                */
            }
        }
        
        return false;
    }

}