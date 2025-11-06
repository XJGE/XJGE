package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 25, 2021
 * <p>
 * Changes how the screen will be divided during split screen mode.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetSplitScreenValue extends TerminalCommand {

    /**
     * Creates a new instance of the setScreenSplit command.
     */
    TCSetSplitScreenValue() {
        super("Changes how the screen will be divided during split screen mode.", 

              "Parameter must be one of: none, vertical, horizontal, trisect, or quarter.",

              "setSplitScreenValue (none|horizontal|vertical|trisect|quarter)");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        if(args.isEmpty()) {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                return new TerminalOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                switch(args.get(0)) {
                    case "none"       -> Window.setSplitScreenType(SplitScreenType.NONE);
                    case "horizontal" -> Window.setSplitScreenType(SplitScreenType.HORIZONTAL);
                    case "vertical"   -> Window.setSplitScreenType(SplitScreenType.VERTICAL);
                    case "trisect"    -> Window.setSplitScreenType(SplitScreenType.TRISECT);
                    case "quarter"    -> Window.setSplitScreenType(SplitScreenType.QUARTER);
                    default -> {
                        return new TerminalOutput(errorInvalidArg(args.get(0), "(none), (horizontal), (vertical), (trisect), or (quarter)"), Color.RED);
                    }
                }
                
                return new TerminalOutput("Split screen value changed: (" + args.get(0) + ")", Color.WHITE);
            }
        }
    }

}