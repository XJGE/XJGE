package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

/**
 * Changes the display device the game window will use. Often this will cause the window to relocate.
 */
final class TCSetMonitor extends TerminalCommand {

    /**
     * Creates a new instance of the setMonitor command.
     */
    TCSetMonitor() {
        super("Changes the display device the game window will use.", 

              useGenericSetter("device"),

              "setDisplayDevice (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));

                if(value > 0 && value < Hardware.getNumMonitors() + 1) {
                    Window.setMonitor(args.get(0));
                    setOutput("Moved the window to monitor " + Window.monitor.id + 
                              " \"" + Window.monitor.name + "\"", 
                              Color.WHITE);
                } else {
                    setOutput("ERROR: Could not find a monitor with an ID of " + value, Color.RED);
                }
            } catch(NumberFormatException e) {
                if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                    Window.setMonitor(args.get(0));
                    setOutput("Moved the window to monitor " + Window.monitor.id + 
                              " \"" + Window.monitor.name + "\"", 
                              Color.WHITE);
                } else {
                    setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                }
            }
        } else {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }

}