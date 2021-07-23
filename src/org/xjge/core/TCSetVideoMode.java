package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

//Created: May 27, 2021

/**
 * Alters the video mode of the display device the game window is currently 
 * using.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetVideoMode extends TerminalCommand {

    /**
     * Creates a new instance of the setVideoMode command.
     */
    TCSetVideoMode() {
        super("Alters the video mode of the display device the game window is " + 
              "currently using.", 

              useGenericSetter("video mode"),

              "setVideoMode (next|prev|<int>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    Window.getMonitor().setVideoMode(args.get(0));
                    setOutput("Changed the current video mode: (" + 
                              Window.monitor.getInfo() + ")", 
                              Color.WHITE);
                } catch(NumberFormatException e) {
                    if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                        Window.getMonitor().setVideoMode(args.get(0));
                        setOutput("Changed the current video mode: (" + 
                                  Window.monitor.getInfo() + ")", 
                                  Color.WHITE);
                    } else {
                        setOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                    }
                }
            }
        }
    }

}