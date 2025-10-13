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
class GridPathSelector {
    
    private int stage;

    private final List<GridSpace> path = new ArrayList<>();

    List<GridSpace> prompt(TurnContext turnContext) {
        switch(stage) {
            case 0 -> {
                for(GridSpace gridSpace : turnContext.gridSpaces.values()) {
                    if(gridSpace.occupyingUnit == turnContext.unit) {
                        gridSpace.status = GridSpaceStatus.SELECTED;
                        path.add(0, gridSpace);

                        //Camera request: look at starting space
                        turnContext.scene.focusOverheadCamera(gridSpace, 0.5f);
                    }
                }
                
                stage = 1;
            }
            case 1 -> {
                GridSpace prevSpace = path.get(path.size() - 1);
                GridSpace nextSpace = handleDirectionalInput(turnContext, prevSpace);

                if(nextSpace != null && nextSpace.type != TYPE_SOLID) {
                    if(path.contains(nextSpace)) {
                        //Remove loop
                        for(int i = path.size() - 1; i > path.indexOf(nextSpace); i--) {
                            path.remove(i);
                        }
                    } else {
                        path.add(nextSpace);
                        nextSpace.previousSpace = prevSpace;
                    }

                    //Camera request: smoothly move toward latest selected space
                    turnContext.scene.focusOverheadCamera(nextSpace, 0.02f);
                }

                boolean validPath = validatePath(turnContext);

                applyIndicators(turnContext, validPath);

                if(validPath && turnContext.unit.buttonPressedOnce(Control.CROSS, 0)) stage = 2;
            }

            case 2 -> {
                return path;
            }
        }

        return null;
    }

    private GridSpace handleDirectionalInput(TurnContext turnContext, GridSpace prev) {
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP, 0)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation - 1));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN, 0)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation + 1));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_LEFT, 0)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation - 1, prev.yLocation, prev.zLocation));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_RIGHT, 0)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation + 1, prev.yLocation, prev.zLocation));
        }
        
        return null;
    }

    private boolean validatePath(TurnContext turnContext) {
        boolean valid = turnContext.unit.moveRange >= path.size() - 1;
        int moveCost = 0;
        
        for(int i = 0; i < path.size(); i++) {
            GridSpace space = path.get(i);
            moveCost += space.muddy ? 2 : 1;
            
            if((i != path.size() - 1 && space.occupyingUnit != null && space.occupyingUnit != turnContext.unit) ||
               moveCost > turnContext.unit.moveRange) {
                valid = false;
            }
        }
        
        return valid;
    }

    private void applyIndicators(TurnContext turnContext, boolean validPath) {
        for(GridSpace space : turnContext.gridSpaces.values()) {
            if(path.contains(space)) {
                if(space.equals(path.get(path.size() - 1))) {
                    space.status = GridSpaceStatus.SELECTED;
                } else {
                    space.status = validPath ? GridSpaceStatus.PATH : GridSpaceStatus.INVALID;
                }
            } else {
                space.status = GridSpaceStatus.NONE;
            }
        }
    }
    
}