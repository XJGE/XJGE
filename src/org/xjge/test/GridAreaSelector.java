package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3i;
import org.xjge.core.Control;
import static org.xjge.test.GridSpace.TYPE_SOLID;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridAreaSelector {
    
    private int stage;
    
    private GridSpace nextSpace;
    private final Vector3i offset = new Vector3i();
    private final Vector3i[] locations;
    private final List<GridSpace> area = new ArrayList<>();
    
    GridAreaSelector(int range) {
        int index = 0;
        int length = range + range + 1;
        locations = new Vector3i[length * length];
        
        for(int y = -range; y < range + 1; y++) {
            for(int x = -range; x < range + 1; x++) {
                locations[index] = new Vector3i(x, 0, y);
                index++;
            }
        }
    }

    List<GridSpace> prompt(TurnContext turnContext) {        
        switch(stage) {
            //Set initial space
            case 0 -> {
                for(GridSpace gridSpace : turnContext.gridSpaces.values()) {
                    if(gridSpace.occupyingUnit == turnContext.unit) {
                        gridSpace.status = GridSpaceStatus.SELECTED;
                        turnContext.scene.focusOverheadCamera(gridSpace, 0.5f);
                        nextSpace = gridSpace;
                    }
                }
                
                stage = 1;
            }
            //Choose candidate area
            case 1 -> {
                GridSpace previousSpace = nextSpace;
                nextSpace = handleDirectionalInput(turnContext, previousSpace);
                
                if(nextSpace != null && nextSpace.type != TYPE_SOLID) {
                    area.clear();
                    
                    for(int i = 0; i < locations.length; i++) {
                        offset.set(nextSpace.xLocation + locations[i].x, 
                                   nextSpace.yLocation + locations[i].y, 
                                   nextSpace.zLocation + locations[i].z);
                        
                        if(turnContext.gridSpaces.containsKey(offset)) {
                            area.add(turnContext.gridSpaces.get(offset));
                        }
                    }
                    
                    turnContext.scene.focusOverheadCamera(nextSpace, 0.1f);
                }
                
                applyIndicators(turnContext);
                
                if(turnContext.unit.buttonPressedOnce(Control.CROSS)) stage = 2;
            }
            case 2 -> {
                return area;
            }
        }
        
        return null;
    }
    
    private GridSpace handleDirectionalInput(TurnContext turnContext, GridSpace prev) {
        Vector3i candidatePos = null;
        
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP)) {
            candidatePos = new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation - 1);
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN)) {
            candidatePos = new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation + 1);
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_LEFT)) {
            candidatePos = new Vector3i(prev.xLocation - 1, prev.yLocation, prev.zLocation);
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_RIGHT)) {
            candidatePos = new Vector3i(prev.xLocation + 1, prev.yLocation, prev.zLocation);
        }

        if(candidatePos != null && turnContext.gridSpaces.containsKey(candidatePos)) {
            return turnContext.gridSpaces.get(candidatePos);
        }

        //No movement or invalid target: stay on current space
        return prev;
    }
    
    private void applyIndicators(TurnContext turnContext) {
        for(GridSpace space : turnContext.gridSpaces.values()) {
            if(area.contains(space)) {
                if(space.equals(nextSpace)) {
                    space.status = GridSpaceStatus.SELECTED;
                } else {
                    space.status = GridSpaceStatus.PATH;
                }
            } else {
                space.status = GridSpaceStatus.NONE;
            }
        }
    }
    
}