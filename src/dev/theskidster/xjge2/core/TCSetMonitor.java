package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCSetMonitor extends TerminalCommand {

    public TCSetMonitor() {
        super("Changes the current visual display device.", 

              useGenericSetter("device"),

              "setDisplayDevice (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));

                if(value > -1 && value < WinKit.getNumMonitors()) {
                    Window.setMonitor(args.get(0));
                } else {
                    setOutput("ERROR: Could not find a display device by the ID of " + value, Color.YELLOW);
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