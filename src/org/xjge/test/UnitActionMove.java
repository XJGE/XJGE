package org.xjge.test;

import java.util.List;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMove extends UnitAction {
    
    private boolean cameraChanged;
    private boolean widgetAdded;
    private boolean[] rtrOutcome = new boolean[2];
    
    private int pathIndex = 1;
    private int meleeStageIndex;
    private int currentTick;
    private int attackCount;
    
    private float pathLerp;
    private float meleeLerp;
    private final float MOVE_SPEED = 6f;
    
    private ComponentUnit targetUnit;
    private Vector3f targetUnitPos;
    private Vector3f meleeDirection;
    private final Timer timer = new Timer();
    
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
                turnContext.scene.setCameraMelee(0.5f);
                cameraChanged = true;
            }
            
            return meleeStage(turnContext);
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
    
    private boolean meleeStage(TurnContext turnContext) {
        if(!turnContext.scene.CameraTransitionComplete()) return false;
        
        if(meleeDirection == null) {
            meleeDirection = new Vector3f(targetUnitPos.x - turnContext.unitPos.x, 
                                          0,
                                          targetUnitPos.z - turnContext.unitPos.z).normalize();
        }
        
        switch(meleeStageIndex) {
            case 0 -> {
                if(currentTick < 2) {
                    if(timer.tick(5)) currentTick++;
                } else {
                    widgetAdded     = false;
                    meleeStageIndex = (attackCount == 1) ? 2 : 1;
                }
            }
            case 1 -> {
                //Back up
                meleeLerp += 0.05f;
                turnContext.unitPos.x -= meleeDirection.x * 0.02f;
                turnContext.unitPos.z -= meleeDirection.z * 0.02f;
                if(meleeLerp >= 1f) {
                    meleeLerp = 0f;
                    meleeStageIndex++;
                    currentTick = 0;
                }
            }
            case 2 -> {
                if(currentTick <= 40) {
                    float t = currentTick / 20f;
                    float offset = (float)Math.sin(t * Math.PI);
                    turnContext.unitPos.x += meleeDirection.x * offset * 0.1f;
                    turnContext.unitPos.z += meleeDirection.z * offset * 0.1f;
                    currentTick++;
                    
                    if(currentTick > 17 && currentTick < 23) {
                        if(!widgetAdded) {
                            UI.addWidget(GLFW_JOYSTICK_1, "melee_qte", new WidgetQTE());
                            widgetAdded = true;
                        }
                        
                        if(turnContext.unit.isPlayer) {
                            //player attacking
                            if(turnContext.unit.buttonPressed(Control.CROSS)) {
                                rtrOutcome[attackCount] = true;
                            }
                        } else if(targetUnit.isPlayer) {
                            //player defending
                            if(targetUnit.buttonPressed(Control.CROSS)) {
                                rtrOutcome[attackCount] = true;
                            }
                        }
                    }
                } else {
                    attackCount++;
                    currentTick = 0;
                    meleeStageIndex = (attackCount > 1) ? 3 : 0;
                }
            }
            case 3 -> {
                System.out.println("A: " + rtrOutcome[0]);
                System.out.println("B: " + rtrOutcome[1]);
                meleeStageIndex = 4;
            }
            case 4 -> {
                
            }
        }
        
        return false;
    }
    
}