package org.xjge.test;

import java.util.List;
import java.util.Random;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import static org.xjge.test.GridSpace.TYPE_SOLID;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UnitActionMove extends UnitAction {
    
    private boolean cameraChanged;
    private boolean widgetAdded;
    private boolean outcomesApplied;
    private final boolean[] attackerOutcome = new boolean[2];
    private final boolean[] defenderOutcome = new boolean[2];
    
    private int pathIndex = 1;
    private int meleeStageIndex;
    private int currentTick;
    private int attackCount;
    
    private float pathLerp;
    private float meleeLerp;
    
    private ComponentUnit targetUnit;
    private Vector3f targetUnitPos;
    private Vector3f meleeDirection;
    private Vector3f nextPosition;
    private Vector3f pathEnd;
    private final Timer timer = new Timer();
    private final Random rand = new Random();
    
    private GridSpace nextSpace;
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
        
        GridSpace from = path.get(pathIndex - 1);
        GridSpace to   = path.get(pathIndex);
        
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
            
            return meleeStage(turnContext, to);
        }
        
        //Advance interpolation
        pathLerp += 0.016f * (to.muddy ? 3f : 6f); //TODO: supply deltaTime
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
    
    private boolean meleeStage(TurnContext turnContext, GridSpace to) {
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
                    outcomesApplied = false;
                    currentTick     = 0;
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
                    
                    if(currentTick > 15 && currentTick < 25) {
                        if(!widgetAdded) {
                            UI.addWidget(GLFW_JOYSTICK_1, "melee_qte", new WidgetMelee());
                            widgetAdded = true;
                        }
                        
                        if(turnContext.unit.isPlayer) {
                            //player attacking
                            if(turnContext.unit.buttonPressedOnce(Control.CROSS, 0)) {
                                attackerOutcome[attackCount] = true;
                            }
                            defenderOutcome[attackCount] = rand.nextBoolean();
                        } else if(targetUnit.isPlayer) {
                            //player defending
                            if(targetUnit.buttonPressedOnce(Control.CROSS, 0)) {
                                defenderOutcome[attackCount] = true;
                            }
                            attackerOutcome[attackCount] = rand.nextBoolean();
                        }
                    }
                    
                    if(currentTick > 25 && !outcomesApplied) {
                        if(attackerOutcome[attackCount] && defenderOutcome[attackCount]) {
                            targetUnit.health--;
                            GridSpace end = path.get(path.size() - 2);
                            pathEnd = new Vector3f(end.xLocation, end.yLocation, end.zLocation);
                            WidgetDamage widget = new WidgetDamage(turnContext.scene.getMeleeCameraSide(), "-1hp (blocked)", Color.RED);
                            UI.addWidget(GLFW_JOYSTICK_1, "damage_" + widget.uuid, widget);
                        } else if(!attackerOutcome[attackCount] && defenderOutcome[attackCount]) {
                            GridSpace end = path.get(path.size() - 2);
                            pathEnd = new Vector3f(end.xLocation, end.yLocation, end.zLocation);
                            WidgetDamage widget = new WidgetDamage(turnContext.scene.getMeleeCameraSide(), "miss!", Color.WHITE);
                            UI.addWidget(GLFW_JOYSTICK_1, "damage_" + widget.uuid, widget);
                        } else if(attackerOutcome[attackCount] && !defenderOutcome[attackCount]) {
                            targetUnit.health -= 2;
                            WidgetDamage widget = new WidgetDamage(turnContext.scene.getMeleeCameraSide(), "-2hp", Color.RED);
                            UI.addWidget(GLFW_JOYSTICK_1, "damage_" + widget.uuid, widget);
                            
                            if(attackCount == 1) {
                                for(int i = 0; i < 2; i++) {
                                    Vector3i nextLocation = new Vector3i(to.xLocation + (int)meleeDirection.x, 
                                                                         to.yLocation + i, 
                                                                         to.zLocation + (int)meleeDirection.z);
                                    
                                    nextPosition = new Vector3f(nextLocation.x, nextLocation.y, nextLocation.z);
                                    pathEnd      = new Vector3f(to.xLocation, to.yLocation, to.zLocation);

                                    if(turnContext.gridSpaces.containsKey(nextLocation)) {
                                        nextSpace = turnContext.gridSpaces.get(nextLocation);
                                        
                                        if(nextSpace.type == TYPE_SOLID) {
                                            nextSpace = null;
                                            GridSpace end = path.get(path.size() - 2);
                                            pathEnd = new Vector3f(end.xLocation, end.yLocation, end.zLocation);
                                        }

                                        attackCount++;
                                        currentTick = 0;
                                        meleeStageIndex = 3;
                                        break;
                                    }
                                }
                            }
                        } else {
                            GridSpace end = path.get(path.size() - 2);
                            pathEnd = new Vector3f(end.xLocation, end.yLocation, end.zLocation);
                            WidgetDamage widget = new WidgetDamage(turnContext.scene.getMeleeCameraSide(), "miss!", Color.WHITE);
                            UI.addWidget(GLFW_JOYSTICK_1, "damage_" + widget.uuid, widget);
                        }
                        
                        outcomesApplied = true;
                    }
                } else {
                    attackCount++;
                    currentTick = 0;
                    meleeStageIndex = (attackCount > 1) ? 3 : 0;
                }
            }
            case 3 -> {
                float t = currentTick / 20f;
                if(t > 1f) t = 1f;
                turnContext.unitPos.lerp(pathEnd, t);

                if(nextSpace != null && nextSpace.type != 1) {
                    targetUnitPos.lerp(nextPosition, t);
                    
                    if(t >= 1f) {
                        GridSpace attackerDest = path.get(path.size() - 1);
                        GridSpace defenderDest = nextSpace;
                        
                        for(GridSpace gs : turnContext.gridSpaces.values()) {
                            if(gs.occupyingUnit == turnContext.unit && gs != attackerDest) {
                                gs.occupyingUnit = null;
                            }
                            if(gs.occupyingUnit == targetUnit && gs != defenderDest) {
                                gs.occupyingUnit = null;
                            }
                        }
                        
                        defenderDest.occupyingUnit = targetUnit;
                        attackerDest.occupyingUnit = turnContext.unit;
                        
                        nextSpace = null;
                        nextPosition = null;
                        pathEnd = null;

                        turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
                        
                        return true;
                    }
                } else {
                    if(t >= 1f) {
                        GridSpace attackerDest = path.get(Math.max(0, path.size() - 2));
                        
                        for(GridSpace gs : turnContext.gridSpaces.values()) {
                            if(gs.occupyingUnit == turnContext.unit && gs != attackerDest) {
                                gs.occupyingUnit = null;
                            }
                        }

                        attackerDest.occupyingUnit = turnContext.unit;
                        
                        nextSpace = null;
                        nextPosition = null;
                        pathEnd = null;

                        turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
                        
                        return true;
                    }
                }

                currentTick++;
                
                return false;
            }
        }
        
        return false;
    }
    
}