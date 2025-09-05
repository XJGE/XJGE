package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.xjge.core.TerminalCommand;
import org.xjge.core.TerminalOutput;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TCSetGameMode extends TerminalCommand implements PropertyChangeListener {

    private Scene3D currentScene;
    
    public TCSetGameMode() {
        super("Used to change the current game mode. This will affect the " + 
              "gameplay rules of entites within the current scene.", 
                
              "Parameter must be one of: battle, or explore.",
              
              "setGameMode (battle|explore)");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        if(args.isEmpty()) {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                return new TerminalOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                switch(args.get(0)) {
                    default -> {
                        return new TerminalOutput(errorInvalidArg(args.get(0), "(battle), or (explore)"), Color.RED);
                    }
                    case "battle"  -> currentScene.setGameMode(new GameModeBattle());
                    case "explore" -> currentScene.setGameMode(new GameModeExplore());
                }
                
                return new TerminalOutput("Game mode value changed: (" + args.get(0) + ")", Color.WHITE);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("SCENE_CHANGED")) {
            if(evt.getNewValue() instanceof Scene3D) {
                currentScene = (Scene3D) evt.getNewValue();
            }
        }
    }

}