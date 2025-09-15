package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.xjge.core.Control;
import org.xjge.core.XJGE;
import static org.xjge.test.GridSpaceStatus.*;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridSelector {

    private boolean initialSpaceSet;
    private boolean basePosSet;
    
    private float currZoomDistance;
    private float prevZoomDistance;
    
    private int stage;
    private int range;
    
    private final Vector3f baseCamPos = new Vector3f();
    private final Vector3f prevCamPos = new Vector3f();
    private final Vector3f nextCamPos = new Vector3f();
    
    private final List<GridSpace> path = new ArrayList<>();
    
    List<GridSpace> prompt(TurnContext turnContext) {
        if(!initialSpaceSet) {
            for(GridSpace gridSpace : turnContext.gridSpaces.values()) if(gridSpace.occupyingUnit == turnContext.unit) {
                gridSpace.status = SELECTED;
                path.add(0, gridSpace);
            }
            
            initialSpaceSet = true;
        }
        
        switch(stage) {
            case 0 -> {
                range = 5; //TODO: add items that extend or reduce this
                stage = 1;
                
                baseCamPos.set(path.get(0).xLocation, path.get(0).yLocation + 10, path.get(0).zLocation + 8);
                nextCamPos.set(path.get(0).xLocation, 5, path.get(0).zLocation + 4);
            }
            case 1 -> {
                GridSpace prevSpace = path.get(path.size() - 1);
                GridSpace nextSpace = null;
                
                if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP)) {
                    nextSpace = turnContext.gridSpaces.get(new Vector3i(prevSpace.xLocation, prevSpace.yLocation, prevSpace.zLocation - 1));
                } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN)) {
                    nextSpace = turnContext.gridSpaces.get(new Vector3i(prevSpace.xLocation, prevSpace.yLocation, prevSpace.zLocation + 1));
                } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_LEFT)) {
                    nextSpace = turnContext.gridSpaces.get(new Vector3i(prevSpace.xLocation - 1, prevSpace.yLocation, prevSpace.zLocation));
                } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_RIGHT)) {
                    nextSpace = turnContext.gridSpaces.get(new Vector3i(prevSpace.xLocation + 1, prevSpace.yLocation, prevSpace.zLocation));
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
                
                if(turnContext.unit.buttonPressed(Control.L1)) {
                    currZoomDistance = XJGE.clampValue(-5.0f, 0f, currZoomDistance + 0.25f);
                } else if(turnContext.unit.buttonPressed(Control.R1)) {
                    currZoomDistance = XJGE.clampValue(-5.0f, 0f, currZoomDistance - 0.25f);
                }

                nextCamPos.set(baseCamPos);

                if(!nextCamPos.equals(prevCamPos) || currZoomDistance != prevZoomDistance) {
                    nextCamPos.add(turnContext.scene.getOverheadCameraDirection().x * currZoomDistance,
                                   turnContext.scene.getOverheadCameraDirection().y * currZoomDistance,
                                   turnContext.scene.getOverheadCameraDirection().z * currZoomDistance);
                }

                prevCamPos.set(nextCamPos);
                prevZoomDistance = currZoomDistance;
                
                boolean validPath = range >= path.size() - 1;
                
                //Determine if the path violates any occupancy rules
                for(int i = 0; i < path.size(); i++) {
                    GridSpace space = path.get(i);
                    if(i != path.size() - 1 && space.occupyingUnit != null && space.occupyingUnit != turnContext.unit) {
                        validPath = false;
                    }
                }
                
                //Apply visual indicators to spaces
                for(GridSpace space : turnContext.gridSpaces.values()) {
                    if(path.contains(space)) {
                        if(space.equals(path.get(path.size() - 1))) {
                            space.status = SELECTED;
                        } else {
                            space.status = (validPath) ? PATH : INVALID;
                        }
                    } else {
                        space.status = NONE;
                    }
                }
                
                if(validPath && turnContext.unit.buttonPressedOnce(Control.CROSS)) stage = 2;
            }
            case 2 -> {
                return path;
            }
        }
        
        turnContext.scene.moveOverheadCamera(nextCamPos, 0.05f);
        
        return null;
    }
    
}