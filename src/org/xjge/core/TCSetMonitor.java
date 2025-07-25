package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 26, 2021
 * <p>
 * Changes the display device the game window will use. Often this will cause 
 * the window to relocate.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetMonitor extends TerminalCommand {

    /**
     * Creates a new instance of the setMonitor command.
     */
    TCSetMonitor() {
        super("Changes the display device the game window will use.", 

              useGenericSetter("device"),

              "setMonitor (<int>)");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        if(!args.isEmpty()) {
            try {
                int index   = Integer.parseInt(args.get(0));
                var monitor = Window.getMonitor(index);
                
                if(Window.setMonitor(monitor)) {
                    return new TerminalOutput("Moved window to monitor \"" + monitor.name + "\" at index " + monitor.index, Color.WHITE);
                } else {
                    return new TerminalOutput("ERROR: Failed to change window monitor. Unable to find a monitor at index " + index, Color.RED);
                }
            } catch(NumberFormatException exception) {
                return new TerminalOutput(errorInvalidArg(args.get(0), "<int>"), Color.RED);
            }
        } else {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }

}