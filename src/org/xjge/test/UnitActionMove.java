package org.xjge.test;

import java.util.List;
import org.xjge.core.XJGE;
import static org.xjge.test.ActionResult.PENDING;
import static org.xjge.test.ActionResult.SUCCESS;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMove extends UnitAction {

    private int pathIndex;
    
    private final List<GridSpace> path;
    
    UnitActionMove(List<GridSpace> path) {
        this.path = path;
    }
    
    @Override
    ActionResult perform(TurnContext turnContext) {
        if(XJGE.tick(15)) {
            if(pathIndex < path.size()) {
                GridSpace space = path.get(pathIndex);

                turnContext.unitPos.x = space.xLocation;
                turnContext.unitPos.z = space.zLocation;

                pathIndex++;

                if(pathIndex < path.size()) space.occupyingUnit = null;
            } else {
                GridSpace currentSpace = path.get(pathIndex - 1);
                
                /*
                if(currentSpace.occupyingUnit != null) {
                    ComponentUnit other = currentSpace.occupyingUnit;

                    //Knock occupying unit into adjacent grid space
                    if(other.team != turnContext.unit.team) {
                        int xOffset = currentSpace.xLocation - currentSpace.previousSpace.xLocation;
                        int zOffset = currentSpace.zLocation - currentSpace.previousSpace.zLocation;

                        Vector3i key = new Vector3i(currentSpace.xLocation + xOffset, 
                                                    currentSpace.yLocation,
                                                    currentSpace.zLocation + zOffset);

                        if(turnContext.gridSpaces.get(key) != null && turnContext.gridSpaces.get(key).type != 1) {
                            other.position.x = turnContext.gridSpaces.get(key).xLocation;
                            other.position.z = turnContext.gridSpaces.get(key).zLocation;
                            turnContext.gridSpaces.get(key).occupyingUnit = other;
                        } else {
                            turnContext.unitPos.x = currentSpace.xLocation;
                            turnContext.unitPos.z = currentSpace.zLocation;
                            other.position.x      = currentSpace.previousSpace.xLocation;
                            other.position.z      = currentSpace.previousSpace.zLocation;
                            currentSpace.previousSpace.occupyingUnit = other;
                        }
                    }
                }
                */

                currentSpace.occupyingUnit = turnContext.unit;
                
                return SUCCESS;
            }
        }
        
        return PENDING;
    }

}