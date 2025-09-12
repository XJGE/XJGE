package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xjge.core.Control;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import static org.xjge.test.ActionCategory.ITEM;
import static org.xjge.test.ActionCategory.MOVE;
import static org.xjge.test.ActionCategory.SPELL;
import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetBattle extends Widget {

    int choice;
    
    private final TurnContext turnContext;
    private State state = State.MENU;
    private UnitAction pendingAction;
    private ActionCategory pendingCategory;
    private GridSelector gridSelector;
    
    private final List<Option> options = new ArrayList<>();
    
    private enum State {
        MENU, TARGET;
    }
    
    private class Option {
        boolean used;
        final String name;
        final Rectangle background;
        
        Option(String name) {
            this.name       = name;
            this.background = new Rectangle(10, 0, 300, 35);
        }
    }
    
    WidgetBattle(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        options.add(new Option("Move"));
        options.add(new Option("Cast Spell"));
        options.add(new Option("Use Item"));
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        switch(state) {
            case MENU -> {
                gridSelector = null;

                if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP)) {
                    choice--;
                } else if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN)) {
                    choice++;
                } else if(turnContext.unit.buttonPressedOnce(Control.CROSS)) {
                    Option option = options.get(choice);
                    
                    if(!option.used) {
                        switch(option.name) {
                            case "Move" -> {
                                pendingCategory = ActionCategory.MOVE;
                                pendingAction   = null;
                                if(gridSelector == null) {
                                    gridSelector = new GridSelector();
                                    turnContext.scene.setCameraOverhead(1.5f);
                                }
                                state = State.TARGET;
                            }

                            case "Cast Spell" -> {
                                
                            }

                            case "Use Item" -> {
                                
                            }
                        }
                    }
                }
            }

            case TARGET -> {
                switch(pendingCategory) {
                    case MOVE -> {
                        
                    }
                    case SPELL -> {
                        //TODO: open spell list and select
                        //SpellSelector
                    }
                    case ITEM -> {
                        //TODO: open item inventory and select
                        //ItemSelector
                    }
                }
                
                if(turnContext.unit.buttonPressedOnce(Control.CIRCLE)) {
                    if(gridSelector != null) {
                        turnContext.gridSpaces.values().forEach(gridSpace -> {
                            gridSpace.status = GridSpaceStatus.NONE;
                        });
                        
                        turnContext.scene.setCameraFollow(turnContext.unit, 1.5f);
                        
                        gridSelector = null;
                    }
                    
                    pendingAction = null;
                    state = State.MENU;
                } else if(turnContext.unit.buttonPressedOnce(Control.CROSS)) {
                    if(pendingAction != null) {
                        turnContext.addAction(pendingCategory, pendingAction);
                        options.get(choice).used = true;
                        state = State.MENU;
                    }
                }
            }
        }

        if(choice == -1) choice = options.size() - 1;
        if(choice == options.size()) choice = 0;
        
        if(gridSelector != null) {
            List<GridSpace> path = gridSelector.prompt(turnContext);
            if(path != null) {
                //pendingAction = new UnitActionMove(path);
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        for(int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            
            option.background.positionY = Window.getResolutionHeight() - (option.background.height * (i + 1)) - (10 * (i + 1));
            
            option.background.render(0.5f, Color.BLACK);
            Font.fallback.drawString(option.name, 
                                     option.background.positionX + 10, 
                                     option.background.positionY + 10, 
                                     (choice == i) ? Color.YELLOW : Color.WHITE, 
                                     1f);
        }
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
        
    }

    @Override
    public void processMouseInput(Mouse mouse) {
        
    }

    @Override
    public void delete() {
    }

}