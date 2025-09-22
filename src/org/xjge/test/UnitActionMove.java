package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMove extends UnitAction {
    
    private boolean cameraChanged;
    
    private int pathIndex = 1;
    
    private float pathLerp;
    private final float MOVE_SPEED = 6f;
    
    private ComponentUnit targetUnit;
    private Vector3f targetUnitPos;
    
    private final List<GridSpace> path;
    
    UnitActionMove(List<GridSpace> path) {
        this.path = path;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        if(path.isEmpty() || pathIndex >= path.size()) {
            turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
            return true;
        }
        
        if(targetUnit == null && path.get(path.size() - 1).occupyingUnit != null) {
            targetUnit    = path.get(path.size() - 1).occupyingUnit;
            targetUnitPos = turnContext.getUnitPos(targetUnit);
        }
        
        if(targetUnit != null && pathIndex == path.size() - 1) {
            turnContext.scene.focusMeleeCamera(turnContext.unitPos, targetUnitPos);
            
            if(!cameraChanged) {
                turnContext.scene.setCameraMelee(0.4f);
                cameraChanged = true;
            }
            //TODO: capture input logic for RTR

            return false; //Busy with RTR
        }
        
        GridSpace from = path.get(pathIndex - 1);
        GridSpace to   = path.get(pathIndex);

        //Advance interpolation
        pathLerp += 0.016f * MOVE_SPEED; //TODO: supply deltaTime
        if(pathLerp > 1f) pathLerp = 1f;

        float newX = XJGE.lerp(from.xLocation, to.xLocation, pathLerp);
        float newZ = XJGE.lerp(from.zLocation, to.zLocation, pathLerp);

        turnContext.unitPos.x = newX;
        turnContext.unitPos.z = newZ;

        //Reached target space, move to the next segment
        if(pathLerp >= 1f) {
            from.occupyingUnit = null;
            to.occupyingUnit   = turnContext.unit;
            pathIndex++;
            pathLerp = 0f;
        }
        
        //Finished path traversal
        if(pathIndex >= path.size()) {
            turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
            return true;
        }
        
        return false;
    }
    
}