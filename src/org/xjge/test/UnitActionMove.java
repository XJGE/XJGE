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
    
    private boolean queueRTR;
    
    private int currentShotIndex;
    private int pathIndex = 1; //Start moving toward the 2nd space
    
    private float shotTimer;
    private float pathLerp;
    private final float MOVE_SPEED = 6f;
    
    private final List<GridSpace> path;
    
    private final List<ShotMelee> shotSequence = new ArrayList<>();
    
    UnitActionMove(List<GridSpace> path) {
        this.path = path;
    }
    
    @Override
    boolean perform(TurnContext turnContext) {
        if(path.isEmpty() || pathIndex >= path.size()) {
            turnContext.scene.setCameraFollow(turnContext.unit, 0.4f);
            return true;
        }
        
        if(!queueRTR && path.get(path.size() - 1).occupyingUnit != null) {
            queueRTR = true;
            setupShotSequence(turnContext);
        }
        
        if(queueRTR && pathIndex == path.size() - 1) {
            //Run the shot sequence
            if(currentShotIndex < shotSequence.size()) {
                ShotMelee shot = shotSequence.get(currentShotIndex);

                //Apply focus + angles through Scene3D
                turnContext.scene.focusMeleeCamera(shot.attackerPos, shot.defenderPos, shot.lerpFactor);
                turnContext.scene.setMeleeCameraAngles(shot.yaw, shot.pitch, shot.lerpFactor);
                turnContext.scene.setCameraMelee(shot.duration);

                //Tick shot timer
                shotTimer += 0.016f; //TODO: supply deltaTime
                if(shotTimer >= shot.duration) {
                    shotTimer = 0f;
                    currentShotIndex++;
                }
            } else {
                // finished all shots, proceed to RTR resolution
                // TODO: begin RTR input/logic here
            }

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
            turnContext.scene.setCameraFollow(turnContext.unit, 0.4f);
            return true;
        }
        
        return false;
    }
    
    private void setupShotSequence(TurnContext turnContext) {
        shotSequence.clear();
        currentShotIndex = 0;
        shotTimer = 0f;

        Vector3f attackerPos = turnContext.unitPos;
        Vector3f defenderPos = turnContext.getUnitPos(path.get(path.size() - 1).occupyingUnit);
        
        //Simple 2-shot example sequence
        shotSequence.add(new ShotMelee(attackerPos, defenderPos, -90f, 52f, 1.5f, 0.1f)); //Lerp to wide
        shotSequence.add(new ShotMelee(attackerPos, defenderPos, 45f, 30f, 1.0f, 1.0f));  //Snap to closeup
    }
    
}