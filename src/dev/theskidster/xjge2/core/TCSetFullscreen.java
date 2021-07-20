package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

//Created: May 25, 2021

/**
 * Changes the game window between fullscreen and windowed modes.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetFullscreen extends TerminalCommand {

    /**
     * Creates a new instance of the setFullscreen command.
     */
    TCSetFullscreen() {
        super("Changes the game window between fullscreen and windowed modes.", 
              
              useGenericShowing("fullscreen mode"), 
              
              "setFullscreen [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;
        
        if(!args.isEmpty()) {
            String parameter = args.get(0);
            
            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                setOutput("Fullscreen changed: (" + value + ")", Color.WHITE);
                Window.setFullscreen(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            Window.setFullscreen(!Window.getFullscreen());
            setOutput("Fullscreen changed: (" + Window.getFullscreen() + ")", Color.WHITE);
        }
    }

}