package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;
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
        /*
        TODO: 
        use input state of the unit instead? This way controllers and keyboards 
        alike will follow the same logic
        
        if(turnContext.unit.buttonPressed(Control.DPAD_UP)) System.out.println("up pressed");
        */

        if(choice == -1) choice = options.size() - 1;
        if(choice == options.size()) choice = 0;
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
        if(action != GLFW_PRESS) return;
        
        switch(state) {
            case MENU -> {
                switch(key) {
                    case GLFW_KEY_UP   -> choice--;
                    case GLFW_KEY_DOWN -> choice++;
                    case GLFW_KEY_ENTER -> {
                        Option option = options.get(choice);
                        
                        if(!option.used) {
                            switch(option.name) {
                                case "Move" -> {
                                    pendingCategory = ActionCategory.MOVE;
                                    pendingAction   = null;
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
            }
            case TARGET -> {
                switch(pendingCategory) {
                    case MOVE -> {
                        //TODO: allow players to traverse grid
                        //List<GridSpace> path = GridSelector.prompt(turnContext);
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
                
                switch(key) {
                    case GLFW_KEY_ESCAPE -> {
                        pendingAction = null;
                        state = State.MENU;
                    }
                    case GLFW_KEY_ENTER -> {
                        if(pendingAction != null) {
                            turnContext.addAction(pendingCategory, pendingAction);
                            options.get(choice).used = true;
                            state = State.MENU;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processMouseInput(Mouse mouse) {
        
    }

    @Override
    public void delete() {
    }

}