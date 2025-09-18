package org.xjge.test;

import java.util.List;
import org.joml.Vector3f;
import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMove extends UnitAction {

    private boolean queueRTR;
    private boolean cameraSet;
    
    private int pathIndex;
    private float t;
    
    private final List<GridSpace> path;
    private static final float MOVE_SPEED = 6f;
    
    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraTarget   = new Vector3f();
    
    UnitActionMove(List<GridSpace> path) {
        this.path = path;
        this.pathIndex = 1; //Start moving toward the 2nd space
        this.t = 0f;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        if(path.isEmpty() || pathIndex >= path.size()) {
            turnContext.scene.setCameraFollow(turnContext.unit, 0.4f);
            return true;
        }
        
        if(!queueRTR && path.get(path.size() - 1).occupyingUnit != null) queueRTR = true;
        
        if(queueRTR && pathIndex == path.size() - 1) {
            if(!cameraSet) {
                cameraPosition.set(10, 0, 10);
                cameraTarget.set(turnContext.unitPos);
                turnContext.scene.setCameraMelee(cameraPosition, cameraTarget, 0.4f);
                cameraSet = true;
            }
            
            //TODO: implement melee RTR
        } else {
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
            if(t >= 1f) {
                from.occupyingUnit = null;
                to.occupyingUnit   = turnContext.unit;

                pathIndex++;
                t = 0f;
            }

            //Finished path traversal
            if(pathIndex >= path.size()) {
                turnContext.scene.setCameraFollow(turnContext.unit, 0.4f);
                return true;
            }
        }
        
        return false;
    }
    
}