package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

final class TCSetFullscreen extends TerminalCommand {

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
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.YELLOW);
            }
        } else {
            Window.setFullscreen(!Window.getFullscreen());
            setOutput("Fullscreen changed: (" + Window.getFullscreen() + ")", Color.WHITE);
        }
    }

}