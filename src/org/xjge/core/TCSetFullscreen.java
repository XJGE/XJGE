package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 25, 2021
 * <p>
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
    public TerminalOutput execute(List<String> args) {
        if(!args.isEmpty()) {
            String parameter = args.get(0);
            
            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Window.setFullscreen(value);
                return new TerminalOutput("Fullscreen changed: (" + value + ")", Color.WHITE);
            } else {
                return new TerminalOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            Window.setFullscreen(!Window.getFullscreen());
            return new TerminalOutput("Fullscreen changed: (" + Window.getFullscreen() + ")", Color.WHITE);
        }
    }

}