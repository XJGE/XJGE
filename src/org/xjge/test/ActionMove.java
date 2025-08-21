package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Input;
import static org.xjge.core.Input.AI_GAMEPAD_1;
import org.xjge.core.UI;
import org.xjge.core.XJGE;
import static org.xjge.test.GridSpaceStatus.INVALID;
import static org.xjge.test.GridSpaceStatus.NO_STATUS;
import static org.xjge.test.GridSpaceStatus.PATH;
import static org.xjge.test.GridSpaceStatus.SELECTED;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ActionMove extends Action {

    private boolean initialSpaceSet;
    private boolean alternate;
    
    private float currZoomDistance;
    private float prevZoomDistance;
    
    private int stage;
    private int pathIndex;
    private int roll;
    
    private final Random random = new Random();
    private final Vector3i closestPlayer = new Vector3i();
    
    private final Vector3f baseCamPos = new Vector3f();
    private final Vector3f prevCamPos = new Vector3f();
    private final Vector3f nextCamPos = new Vector3f();
    
    private final List<GridSpace> path = new ArrayList<>();
    
    @Override
    boolean perform(Scene3D scene, ComponentUnit activeUnit, Map<UUID, ComponentUnit> units, Map<Vector3i, GridSpace> spaces) {
        if(!initialSpaceSet) {
            spaces.values().forEach(space -> {
                if(space.occupyingUnit == activeUnit) space.status = SELECTED;
            });
            
            for(GridSpace space : spaces.values()) if(space.occupyingUnit == activeUnit) {
                path.add(0, space);
                baseCamPos.set(path.get(0).xLocation, path.get(0).yLocation + 10, path.get(0).zLocation + 8);
            }
            
            initialSpaceSet = true;
        }
        
        switch(stage) {
            case 0 -> {
                //TODO: refine mechanics that allows players to gauge their rolls
                
                roll  = 20; //random.nextInt(5) + 1;
                stage = 1;
                
                UI.addWidget(GLFW_JOYSTICK_1, "roll_outcome", new WidgetRoll(roll));
                
                nextCamPos.set(activeUnit.position.x, 5, activeUnit.position.z + 4);
            }
            
            case 1 -> {
                //TODO: A* pathfinding will be needed here eventually
                if(activeUnit.name.equals("enemy") && XJGE.tick(10)) {
                    spaces.forEach((location, space) -> {
                        if(space.occupyingUnit != null && space.occupyingUnit.name.equals("player")) {
                            closestPlayer.set(location);
                        }
                    });
                    
                    alternate = !alternate;
                    
                    if(path.size() - 1 < roll) {
                        if(path.isEmpty()) {
                            if(closestPlayer.x < activeUnit.position.x) {
                                Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_LEFT, (alternate) ? 0 : 1);
                            } else if(closestPlayer.x > activeUnit.position.x) {
                                Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_RIGHT, (alternate) ? 0 : 1);
                            } else if(closestPlayer.z > activeUnit.position.z) {
                                Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_DOWN, (alternate) ? 0 : 1);
                            } else {
                                Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_UP, (alternate) ? 0 : 1);
                            }
                        } else {
                            if(path.get(path.size() - 1).xLocation == closestPlayer.x && 
                               path.get(path.size() - 1).zLocation == closestPlayer.z) {
                                Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.CROSS, (alternate) ? 0 : 1);
                            } else {
                                if(closestPlayer.x < path.get(path.size() - 1).xLocation) {
                                    Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_LEFT, (alternate) ? 0 : 1);
                                } else if(closestPlayer.x > path.get(path.size() - 1).xLocation) {
                                    Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_RIGHT, (alternate) ? 0 : 1);
                                } else if(closestPlayer.z > path.get(path.size() - 1).zLocation) {
                                    Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_DOWN, (alternate) ? 0 : 1);
                                } else {
                                    Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.DPAD_UP, (alternate) ? 0 : 1);
                                } 
                            }
                        }
                    } else {
                        Input.setVirtualGamepadInput(AI_GAMEPAD_1, Control.CROSS, (alternate) ? 0 : 1);
                    }
                }
                
                GridSpace prevSpace = path.get(path.size() - 1);
                GridSpace nextSpace = null;
                
                if(activeUnit.buttonPressedOnce(Control.DPAD_UP)) {
                    nextSpace = spaces.get(new Vector3i(prevSpace.xLocation, prevSpace.yLocation, prevSpace.zLocation - 1));
                } else if(activeUnit.buttonPressedOnce(Control.DPAD_DOWN)) {
                    nextSpace = spaces.get(new Vector3i(prevSpace.xLocation, prevSpace.yLocation, prevSpace.zLocation + 1));
                } else if(activeUnit.buttonPressedOnce(Control.DPAD_LEFT)) {
                    nextSpace = spaces.get(new Vector3i(prevSpace.xLocation - 1, prevSpace.yLocation, prevSpace.zLocation));
                } else if(activeUnit.buttonPressedOnce(Control.DPAD_RIGHT)) {
                    nextSpace = spaces.get(new Vector3i(prevSpace.xLocation + 1, prevSpace.yLocation, prevSpace.zLocation));
                }
                
                //Remove redundant spaces from path if it overlaps itself
                if(nextSpace != null && nextSpace.type != 1) {
                    if(path.contains(nextSpace)) {
                        for(int i = path.size() - 1; i > path.indexOf(nextSpace); i--) path.remove(i);
                    } else {
                        path.add(nextSpace);
                        nextSpace.previousSpace = prevSpace;
                    }
                    
                    baseCamPos.set(nextSpace.xLocation, nextSpace.yLocation + 10, nextSpace.zLocation + 8);
                }
                
                if(activeUnit.buttonPressed(Control.CIRCLE)) {
                    currZoomDistance = XJGE.clampValue(-5.0f, 0f, currZoomDistance + 0.25f);
                } else if(activeUnit.buttonPressed(Control.SQUARE)) {
                    currZoomDistance = XJGE.clampValue(-5.0f, 0f, currZoomDistance - 0.25f);
                }
                
                nextCamPos.set(baseCamPos);
                
                if(!nextCamPos.equals(prevCamPos) || currZoomDistance != prevZoomDistance) {
                    nextCamPos.add(scene.camera.direction.x * currZoomDistance,
                                   scene.camera.direction.y * currZoomDistance,
                                   scene.camera.direction.z * currZoomDistance);
                }
                
                scene.camera.moveTo(nextCamPos, 0.05f);
                
                prevCamPos.set(nextCamPos);
                prevZoomDistance = currZoomDistance;
                
                boolean validPath = roll >= path.size() - 1;
                
                //Determine if the path violates any occupancy rules
                for(int i = 0; i < path.size(); i++) {
                    GridSpace space = path.get(i);
                    if(i != path.size() - 1 && space.occupyingUnit != null && space.occupyingUnit != activeUnit) {
                        validPath = false;
                    }
                }
                
                //Apply visual indicators to spaces
                for(GridSpace space : spaces.values()) {
                    if(path.contains(space)) {
                        if(space.equals(path.get(path.size() - 1))) {
                            space.status = SELECTED;
                        } else {
                            space.status = (validPath) ? PATH : INVALID;
                        }
                    } else {
                        space.status = NO_STATUS;
                    }
                }
                
                if(validPath && activeUnit.buttonPressedOnce(Control.CROSS)) stage = 2;
            }
            
            case 2 -> {
                if(XJGE.tick(15)) {
                    if(pathIndex < path.size()) {
                        GridSpace space = path.get(pathIndex);
                        
                        activeUnit.position.x = space.xLocation;
                        activeUnit.position.z = space.zLocation;
                        
                        pathIndex++;
                        
                        if(pathIndex < path.size()) space.occupyingUnit = null;
                    } else {
                        GridSpace currentSpace = path.get(pathIndex - 1);
                        
                        if(currentSpace.occupyingUnit != null) {
                            ComponentUnit other = currentSpace.occupyingUnit;
                            
                            //Knock occupying unit into adjacent grid space
                            if(other.team != activeUnit.team) {
                                int xOffset = currentSpace.xLocation - currentSpace.previousSpace.xLocation;
                                int zOffset = currentSpace.zLocation - currentSpace.previousSpace.zLocation;
                                
                                Vector3i key = new Vector3i(currentSpace.xLocation + xOffset, 
                                                            currentSpace.yLocation,
                                                            currentSpace.zLocation + zOffset);
                                
                                if(spaces.get(key) != null && spaces.get(key).type != 1) {
                                    other.position.x = spaces.get(key).xLocation;
                                    other.position.z = spaces.get(key).zLocation;
                                    spaces.get(key).occupyingUnit = other;
                                } else {
                                    activeUnit.position.x = currentSpace.xLocation;
                                    activeUnit.position.z = currentSpace.zLocation;
                                    other.position.x      = currentSpace.previousSpace.xLocation;
                                    other.position.z      = currentSpace.previousSpace.zLocation;
                                    currentSpace.previousSpace.occupyingUnit = other;
                                }
                            }
                        }
                        
                        currentSpace.occupyingUnit = activeUnit;
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

}