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
    private float t;
    
    private final List<GridSpace> path;
    private static final float MOVE_SPEED = 7f;
    
    UnitActionMove(List<GridSpace> path) {
        this.path = path;
        this.pathIndex = 1; //Start moving toward the 2nd space
        this.t = 0f;
    }
    
    @Override
    ActionResult perform(TurnContext turnContext) {
        if(path.isEmpty() || pathIndex >= path.size()) return SUCCESS;
        
        GridSpace from = path.get(pathIndex - 1);
        GridSpace to   = path.get(pathIndex);

        //Advance interpolation
        t += 0.016f * MOVE_SPEED; //TODO: supply deltaTime
        if(t > 1f) t = 1f;
        
        float newX = XJGE.lerp(from.xLocation, to.xLocation, t);
        float newZ = XJGE.lerp(from.zLocation, to.zLocation, t);
        
        turnContext.unitPos.x = newX;
        turnContext.unitPos.z = newZ;
        
        //Reached target space, move to the next segment
        if (t >= 1f) {
            from.occupyingUnit = null;
            to.occupyingUnit   = turnContext.unit;
            
            pathIndex++;
            t = 0f;
        }
        
        //Finished path traversal
        if(pathIndex >= path.size()) return SUCCESS;
        
        return PENDING;
    }
    
}