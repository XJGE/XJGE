package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCSetMonitor extends TerminalCommand {

    TCSetMonitor() {
        super("Changes the current monitor the game window use.", 

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
                } else {
                    setOutput("ERROR: Could not find a monitor with an ID of " + value, Color.YELLOW);
                }
            } catch(NumberFormatException e) {
                if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                    Window.setMonitor(args.get(0));
                } else {
                    setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.YELLOW);
                }
            }
        } else {
            setOutput(errorNotEnoughArgs(1), Color.YELLOW);
        }
    }

}