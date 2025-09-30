package org.xjge.test;

import java.util.List;
import org.xjge.core.Control;
import org.xjge.core.Timer;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionFlash extends UnitAction {
    
    private boolean beginSlowdown;
    private boolean unitMoved;
    
    private int tickCount;
    private int tickSpeed = 4;
    private int currentIndex;
    private int stage;
    
    private final Timer selectTimer   = new Timer();
    private final Timer slowdownTimer = new Timer();
    private final Timer waitTimer     = new Timer();
    private final List<GridSpace> area;
    
    UnitActionFlash(List<GridSpace> area) {
        this.area = area;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        switch(stage) {
            //Handle input
            case 0 -> {
                if(turnContext.unit.buttonPressedOnce(Control.CROSS) && !beginSlowdown) {
                    beginSlowdown = true;
                    stage = 1;
                }
            }
            //Apply slowdown
            case 1 -> {
                if(slowdownTimer.tick(5)) {
                    tickSpeed = 4 + tickCount; 
                    tickCount++;

                    if(tickCount > 25) stage = 2;
                }
            }
            //Stop the selection timer and teleport the unit
            case 2 -> {
                if(waitTimer.tick(5, 30, true)) {
                    if(waitTimer.getTime() == 2) {
                        if(!unitMoved) {
                            //Reset occupancy state of current grid space
                            for(GridSpace space : turnContext.gridSpaces.values()) {
                                if(space.occupyingUnit == turnContext.unit) space.occupyingUnit = null;
                            }
                            
                            GridSpace selectedSpace = area.get(currentIndex);
                            selectedSpace.occupyingUnit = turnContext.unit;
                            selectedSpace.status = GridSpaceStatus.NONE;
                            turnContext.unitPos.set(selectedSpace.xLocation, selectedSpace.yLocation, selectedSpace.zLocation);

                            unitMoved = true;
                        }
                    } else if(waitTimer.getTime() == 4) {
                        
                        turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
                        return true;
                    }
                }
            }
        }
        
        if(stage != 2) {
            if(selectTimer.tick(tickSpeed)) {
                currentIndex = (currentIndex + 1 >= area.size()) ? 0 : currentIndex + 1;
            }
            
            for(int i = 0; i < area.size(); i++) {
                area.get(i).status = (i == currentIndex) ? GridSpaceStatus.SELECTED : GridSpaceStatus.NONE;
            }
        }
        
        return false;
    }

}