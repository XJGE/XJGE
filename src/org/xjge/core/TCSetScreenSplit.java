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
final class TCSetScreenSplit extends TerminalCommand {

    /**
     * Creates a new instance of the setScreenSplit command.
     */
    TCSetScreenSplit() {
        super("Changes how the screen will be divided during split screen mode.", 

              "Parameter must be one of: none, vertical, horizontal, trisect, or quarter.",

              "setSplitType (none|horizontal|vertical|trisect|quarter)");
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
                switch(args.get(0)) {
                    case "none"       -> XJGE.setScreenSplit(Split.NONE);
                    case "horizontal" -> XJGE.setScreenSplit(Split.HORIZONTAL);
                    case "vertical"   -> XJGE.setScreenSplit(Split.VERTICAL);
                    case "trisect"    -> XJGE.setScreenSplit(Split.TRISECT);
                    case "quarter"    -> XJGE.setScreenSplit(Split.QUARTER);

                    default -> setOutput(errorInvalidArg(args.get(0), "(none), (horizontal), (vertical), (trisect), or (quarter)"), Color.RED);
                }
            }
        }
    }

}