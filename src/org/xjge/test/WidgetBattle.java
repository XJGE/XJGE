package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_4;
import org.xjge.core.Control;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
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
        MENU, TARGET, CONFIRM;
    }
    
    private class Option {
        boolean used;
        final String name;
        final Rectangle background;
        final ActionCategory category;
        
        Option(String name, ActionCategory category) {
            this.name       = name;
            this.category   = category;
            this.background = new Rectangle(10, 0, 300, 35);
        }
    }
    
    WidgetBattle(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        options.add(new Option("Move", MOVE));
        options.add(new Option("Cast Spell", SPELL));
        options.add(new Option("Use Item", ITEM));
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(!turnContext.actionsInProgress()) {
            switch(state) {
                case MENU -> handleMenuInput();
                case TARGET -> handleTargetInput();
                case CONFIRM -> handleConfirmInput();
            }
        }

        //Wrap choice
        if(choice < 0) choice = options.size() - 1;
        if(choice >= options.size()) choice = 0;

        //Handle grid selector (continuous prompt)
        if(gridSelector != null) {
            List<GridSpace> path = gridSelector.prompt(turnContext);
            if(path != null && pendingAction == null && pendingCategory == ActionCategory.MOVE) {
                pendingAction = new UnitActionMove(path);
                commitPendingAction();
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        for(int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            
            option.background.positionY = Window.getResolutionHeight() - (option.background.height * (i + 1)) - (10 * (i + 1));
            
            option.background.render(0.5f, Color.BLACK);
            Font.fallback.drawString(((choice == i) ? "> " : "  ") + option.name, 
                                     option.background.positionX + 10, 
                                     option.background.positionY + 10, 
                                     (option.used) ? Color.GRAY : Color.WHITE, 
                                     1f);
            
            if(turnContext.unit.inputDeviceID == KEY_MOUSE_COMBO) {
                Font.fallback.drawString("[SPACE] - Select Action", 10, 10, Color.YELLOW, 1f);
                
                Font.fallback.drawString("[ARROW KEYS] - Nav Menu", 310, 10, Color.YELLOW, 1f);

                switch(state) {
                    case MENU   -> Font.fallback.drawString("[Q] - End Turn", 600, 10, Color.YELLOW, 1f);
                    case TARGET -> Font.fallback.drawString("[Q] - Cancel", 600, 10, Color.YELLOW, 1f);
                }
            } else if(turnContext.unit.inputDeviceID >= GLFW_JOYSTICK_1 && turnContext.unit.inputDeviceID <= GLFW_JOYSTICK_4) {
                //TODO: add controller input hints
            }
        }
        
        Font.fallback.drawString("Health " + turnContext.unit.health, 
                                 Window.getResolutionWidth() - 120, 
                                 Window.getResolutionHeight() - 20, 
                                 Color.RED, 1f);
        
        Font.fallback.drawString("Mana " + turnContext.unit.mana, 
                                 Window.getResolutionWidth() - 120, 
                                 Window.getResolutionHeight() - 40, 
                                 Color.CYAN, 1f);
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {}

    @Override
    public void processKeyboardInput(int key, int action, int mods) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {}
    
    private void handleMenuInput() {
        gridSelector = null;
        
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP)) choice--;
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN)) choice++;
        
        if(turnContext.unit.buttonPressedOnce(Control.CIRCLE)) {
            if(state == State.MENU) turnContext.endTurn();
        } else if(turnContext.unit.buttonPressedOnce(Control.CROSS)) {
            Option option = options.get(choice);
            
            if(!option.used) {
                pendingCategory = option.category;
                pendingAction   = null;

                switch(option.category) {
                    case MOVE -> {
                        gridSelector = new GridSelector();
                        
                        //Snap overhead camera to the units starting space
                        GridSpace unitSpace = turnContext.gridSpaces.values().stream()
                            .filter(space -> space.occupyingUnit == turnContext.unit)
                            .findFirst()
                            .orElse(null); 
                        
                        if(unitSpace != null) turnContext.scene.snapOverheadCamera(unitSpace);
                        
                        turnContext.scene.setCameraOverhead(0.5f);
                        state = State.TARGET;
                    }
                    case SPELL, ITEM -> {
                        //TODO: open selector UIs
                        state = State.TARGET;
                    }
                }
            }
        }
    }

    private void handleTargetInput() {
        if(turnContext.unit.buttonPressedOnce(Control.CIRCLE)) {
            resetTargeting();
            turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
            state = State.MENU;
        } else if(turnContext.unit.buttonPressedOnce(Control.CROSS) && pendingAction != null) {
            state = State.CONFIRM;
        }
    }

    private void handleConfirmInput() {
        //Could extend with "are you sure?" UI
        commitPendingAction();
    }

    private void commitPendingAction() {
        turnContext.addAction(pendingCategory, pendingAction);
        options.get(choice).used = true;
        resetTargeting();
        state = State.MENU;
    }

    private void resetTargeting() {
        if(gridSelector != null) {
            turnContext.gridSpaces.values().forEach(gs -> gs.status = GridSpaceStatus.NONE);
            gridSelector = null;
        }
        
        pendingAction = null;
    }

}