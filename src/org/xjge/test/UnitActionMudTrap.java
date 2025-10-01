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
                
            }
        }
        
        return false;
    }

}