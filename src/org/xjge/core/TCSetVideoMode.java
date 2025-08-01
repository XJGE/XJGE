package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 27, 2021
 * <p>
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
    public TerminalOutput execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                return new TerminalOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    Window.getMonitor().setVideoMode(args.get(0));
                    return new TerminalOutput("Changed the current video mode: (" + 
                              Window.getMonitor().getInfo() + ")", 
                              Color.WHITE);
                } catch(NumberFormatException e) {
                    if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                        Window.getMonitor().setVideoMode(args.get(0));
                        return new TerminalOutput("Changed the current video mode: (" + 
                                  Window.getMonitor().getInfo() + ")", 
                                  Color.WHITE);
                    } else {
                        return new TerminalOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                    }
                }
            }
        }
    }

}