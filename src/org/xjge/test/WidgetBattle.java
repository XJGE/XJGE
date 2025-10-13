package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_4;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Timer;
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

    private boolean renderSubMenus;
    
    int mainMenuChoice;
    int subMenuChoice;
    
    private final TurnContext turnContext;
    private State state = State.MAINMENU;
    private UnitAction pendingAction;
    private ActionCategory pendingCategory;
    private String pendingSpell;
    private String errorMessage;
    private GridAreaSelector areaSelector;
    private GridPathSelector pathSelector;
    private UnitSelector unitSelector;
    private Timer errorTimer = new Timer();
    
    private final Rectangle[] rectangles = new Rectangle[3];
    private final List<Option> options = new ArrayList<>();
    
    private enum State {
        MAINMENU, SUBMENU, TARGET, CONFIRM;
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
        
        for(int i = 0; i < rectangles.length; i++) rectangles[i] = new Rectangle(50, 0, 300, 35);
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(!turnContext.actionsInProgress()) {
            switch(state) {
                case MAINMENU -> handleMainMenuInput();
                case SUBMENU -> handleSubMenuInput();
                case TARGET -> handleTargetInput();
                case CONFIRM -> handleConfirmInput();
            }
        }

        //Wrap mainMenuChoice
        if(mainMenuChoice < 0) mainMenuChoice = options.size() - 1;
        if(mainMenuChoice >= options.size()) mainMenuChoice = 0;
        
        //Wrap subMenuChoice
        if(subMenuChoice < 0) subMenuChoice = options.size() - 1;
        if(subMenuChoice >= options.size()) subMenuChoice = 0;

        //Handle path selector
        if(pathSelector != null) {
            List<GridSpace> path = pathSelector.prompt(turnContext);
            if(path != null && pendingAction == null && pendingCategory == ActionCategory.MOVE) {
                pendingAction = new UnitActionMove(path);
                commitPendingAction();
            }
        }
        
        //Handle area selector
        if(areaSelector != null) {
            List<GridSpace> area = areaSelector.prompt(turnContext);
            if(area != null && pendingAction == null && pendingSpell != null && pendingCategory == ActionCategory.SPELL) {
                pendingAction = switch(pendingSpell) {
                    default -> new UnitActionFlash(area);
                    case "Mud Trap" -> new UnitActionMudTrap(area);
                };
                commitPendingAction();
            }
        }
        
        //Handle unit selector
        if(unitSelector != null) {
            ComponentUnit targetUnit = unitSelector.prompt(turnContext);
            
            if(targetUnit != null && pendingAction == null && pendingCategory == ActionCategory.SPELL) {
                pendingAction = new UnitActionManabolt(targetUnit);
                commitPendingAction();
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        for(int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            
            int yOffset = (i == 2 && renderSubMenus && pendingCategory.equals(SPELL)) 
                        ? 135 
                        : 0;
            option.background.positionY = (Window.getResolutionHeight() - yOffset) - (option.background.height * (i + 1)) - (10 * (i + 1));
            
            option.background.render(0.5f, Color.BLACK);
            Font.fallback.drawString(((mainMenuChoice == i) ? "> " : "  ") + option.name, 
                                     option.background.positionX + 10, 
                                     option.background.positionY + 10, 
                                     (option.used) ? Color.GRAY : Color.WHITE, 
                                     1f);
            
            if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
                Font.fallback.drawString("[SPACE] - Select Action", 10, 10, Color.YELLOW, 1f);
                
                Font.fallback.drawString("[ARROW KEYS] - Nav Menu", 310, 10, Color.YELLOW, 1f);

                switch(state) {
                    case MAINMENU -> Font.fallback.drawString("[Q] - End Turn", 600, 10, Color.YELLOW, 1f);
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
        
        if(errorMessage != null) {
            Font.fallback.drawString(errorMessage, 10, 40, Color.RED, 1f);
            
            if(errorTimer.tick(4, 60, true) && errorTimer.getTime() >= 3) {
                errorTimer.reset();
                errorMessage = null;
            }
        }
        
        /*
        Font.fallback.drawString("Mana " + turnContext.unit.mana, 
                                 Window.getResolutionWidth() - 120, 
                                 Window.getResolutionHeight() - 40, 
                                 Color.CYAN, 1f);
        */
        
        //Render submenus
        if(renderSubMenus) {
            if(pendingCategory != null) {
                switch(pendingCategory) {
                    case SPELL -> {
                        for(int i = 0; i < turnContext.unit.spells.size(); i++) {
                            Spell spell = turnContext.unit.spells.get(i);
                            
                            rectangles[i].positionY = (Window.getResolutionHeight() - 90) - (rectangles[i].height * (i + 1)) - (10 * (i + 1));
                            rectangles[i].render(0.5f, Color.BLACK);
                            
                            Font.fallback.drawString(((subMenuChoice == i) ? "> " : "  ") + spell.name(), 
                                     rectangles[i].positionX + 10, 
                                     rectangles[i].positionY + 10, 
                                     Color.CYAN, 1f);
                            
                            /*
                            TODO: I'm gonna skip mana cost for the MVP since we want to stress test here
                            
                            Font.fallback.drawString(spell.cost() + " SP", 
                                     rectangles[i].positionX + rectangles[i].width - 60, 
                                     rectangles[i].positionY + 10, 
                                     Color.CYAN, 1f);
                            */
                        }
                    }
                    case ITEM -> {
                        for(int i = 0; i < turnContext.unit.items.size(); i++) {
                            String itemName = turnContext.unit.items.get(i);
                            
                            rectangles[i].positionY = (Window.getResolutionHeight() - 135) - (rectangles[i].height * (i + 1)) - (10 * (i + 1));
                            rectangles[i].render(0.5f, Color.BLACK);
                            
                            Font.fallback.drawString(((subMenuChoice == i) ? "> " : "  ") + itemName, 
                                     rectangles[i].positionX + 10, 
                                     rectangles[i].positionY + 10, 
                                     Color.YELLOW, 1f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {}

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {}
    
    private void handleMainMenuInput() {
        areaSelector = null;
        pathSelector = null;
        renderSubMenus = false;
        
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP, 0)) mainMenuChoice--;
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN, 0)) mainMenuChoice++;
        
        if(turnContext.unit.buttonPressedOnce(Control.CIRCLE, 0)) {
            if(state == State.MAINMENU) turnContext.endTurn();
        } else if(turnContext.unit.buttonPressedOnce(Control.CROSS, 0)) {
            Option option = options.get(mainMenuChoice);
            
            if(!option.used) {
                pendingCategory = option.category;
                pendingAction   = null;

                switch(option.category) {
                    case MOVE -> {
                        pathSelector = new GridPathSelector();
                        
                        //Snap overhead camera to the units starting space
                        GridSpace unitSpace = turnContext.gridSpaces.values().stream()
                            .filter(space -> space.occupyingUnit == turnContext.unit)
                            .findFirst()
                            .orElse(null); 
                        
                        if(unitSpace != null) {
                            turnContext.scene.snapOverheadCamera(unitSpace.xLocation, unitSpace.yLocation, unitSpace.zLocation);
                        }
                        
                        turnContext.scene.setCameraOverhead(0.5f);
                        state = State.TARGET;
                    }
                    case SPELL, ITEM -> {
                        subMenuChoice = 0;
                        state = State.SUBMENU;
                    }
                }
            }
        }
    }
    
    private void handleSubMenuInput() {
        renderSubMenus = true;
        
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_UP, 0)) subMenuChoice--;
        if(turnContext.unit.buttonPressedOnce(Control.DPAD_DOWN, 0)) subMenuChoice++;
        
        if(turnContext.unit.buttonPressedOnce(Control.CIRCLE, 0)) {
            if(state == State.SUBMENU) state = State.MAINMENU;
        } else if(turnContext.unit.buttonPressedOnce(Control.CROSS, 0)) {
            switch(options.get(mainMenuChoice).category) {
                case SPELL -> {
                    pendingSpell = turnContext.unit.spells.get(subMenuChoice).name();
                    
                    //TODO: this switch is fine for 3 spells but will become unmanagable if more are added later
                    switch(pendingSpell) {
                        case "Flash", "Mud Trap" -> {
                            areaSelector = (pendingSpell.equals("Flash")) 
                                         ? new GridAreaSelector(2, -1) 
                                         : new GridAreaSelector(1, 8);
                            
                            //Snap overhead camera to the units starting space
                            GridSpace unitSpace = turnContext.gridSpaces.values().stream()
                                .filter(space -> space.occupyingUnit == turnContext.unit)
                                .findFirst()
                                .orElse(null);
                            
                            if(unitSpace != null) {
                                turnContext.scene.snapOverheadCamera(unitSpace.xLocation, unitSpace.yLocation, unitSpace.zLocation);
                            }
                            
                            turnContext.scene.setCameraOverhead(0.5f);
                            state = State.TARGET;
                        }
                        case "Manabolt" -> {
                            List<Entity> units = new ArrayList<>();
                            
                            turnContext.entities.values().forEach(entity -> {
                                if(entity.hasComponent(ComponentUnit.class) && entity.hasComponent(ComponentPosition.class) &&
                                   entity.getComponent(ComponentUnit.class) != turnContext.unit) {
                                    units.add(entity);
                                }
                            });
                            
                            if(units.isEmpty()) {
                                errorMessage = "No selectable units present, battle should end.";
                            } else {
                                unitSelector = new UnitSelector(units);
                                state = State.TARGET;
                            }
                        }
                    }
                    
                    state = State.TARGET;
                }
                case ITEM -> {
                    
                }
            }
        }
    }

    private void handleTargetInput() {
        if(turnContext.unit.buttonPressedOnce(Control.CIRCLE, 0)) {
            resetTargeting();
            turnContext.scene.setCameraFollow(turnContext.unit, 0.5f);
            state = (renderSubMenus) ? State.SUBMENU : State.MAINMENU;
        } else if(turnContext.unit.buttonPressedOnce(Control.CROSS, 0) && pendingAction != null) {
            state = State.CONFIRM;
        }
    }

    private void handleConfirmInput() {
        //Could extend with "are you sure?" UI
        commitPendingAction();
    }

    private void commitPendingAction() {
        turnContext.addAction(pendingCategory, pendingAction);
        options.get(mainMenuChoice).used = true;
        resetTargeting();
        state = State.MAINMENU;
    }

    private void resetTargeting() {
        if(areaSelector != null) {
            turnContext.gridSpaces.values().forEach(gs -> gs.status = GridSpaceStatus.NONE);
            areaSelector = null;
        }
        
        if(pathSelector != null) {
            turnContext.gridSpaces.values().forEach(gs -> gs.status = GridSpaceStatus.NONE);
            pathSelector = null;
        }
        
        if(unitSelector != null) unitSelector = null;
        
        pendingAction = null;
        pendingSpell = null;
    }

}