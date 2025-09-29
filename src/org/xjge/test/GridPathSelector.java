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
    
    private boolean initialSpaceSet;
    
    private int stage;
    private int range;

    private final List<GridSpace> path = new ArrayList<>();

    List<GridSpace> prompt(TurnContext turnContext) {
        if(!initialSpaceSet) {
            for(GridSpace gridSpace : turnContext.gridSpaces.values()) {
                if(gridSpace.occupyingUnit == turnContext.unit) {
                    gridSpace.status = GridSpaceStatus.SELECTED;
                    path.add(0, gridSpace);

                    //Camera request: look at starting space
                    turnContext.scene.focusOverheadCamera(gridSpace, 0.5f);
                }
            }
            initialSpaceSet = true;
        }

        switch(stage) {
            case 0 -> {
                range = 5; // TODO: factor in modifiers
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

                //Zoom control (delegated to scene/camera)
                if(turnContext.unit.buttonPressed(Control.L1)) {
                    turnContext.scene.adjustOverheadZoom(+0.25f);
                } else if(turnContext.unit.buttonPressed(Control.R1)) {
                    turnContext.scene.adjustOverheadZoom(-0.25f);
                }

                boolean validPath = validatePath(turnContext);

                applyIndicators(turnContext, validPath);

                if(validPath && turnContext.unit.buttonPressedOnce(Control.CROSS)) stage = 2;
            }

            case 2 -> {
                return path;
            }
        }

        return null;
    }

    private GridSpace handleDirectionalInput(TurnContext turnContext, GridSpace prev) {
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation - 1));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation, prev.yLocation, prev.zLocation + 1));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_LEFT)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation - 1, prev.yLocation, prev.zLocation));
        } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_RIGHT)) {
            return turnContext.gridSpaces.get(new Vector3i(prev.xLocation + 1, prev.yLocation, prev.zLocation));
        }
        
        return null;
    }

    private boolean validatePath(TurnContext turnContext) {
        boolean valid = range >= path.size() - 1;
        for(int i = 0; i < path.size(); i++) {
            GridSpace space = path.get(i);
            if(i != path.size() - 1 && space.occupyingUnit != null && space.occupyingUnit != turnContext.unit) {
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