package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class TCSetVideoMode extends TerminalCommand {

    TCSetVideoMode() {
        super("Alters the video mode of the current monitor.", 

              useGenericSetter("video mode"),

              "setVideoMode (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.YELLOW);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.YELLOW);
            } else {
                try {
                    int value = Integer.parseInt(args.get(0));
                    Window.getMonitor().setVideoMode(args.get(0));
                } catch(NumberFormatException e) {
                    if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                        Window.getMonitor().setVideoMode(args.get(0));
                    } else {
                        setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.YELLOW);
                    }
                }
            }
        }
    }

}