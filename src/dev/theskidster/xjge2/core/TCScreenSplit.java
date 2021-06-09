package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

final class TCScreenSplit extends TerminalCommand {

    TCScreenSplit() {
        super("Changes what kind of split is used for splitscreen play. Otherwise hidden " +
              "viewports will use either the default camera or whichever was most " + 
              "recently set.", 

              "Parameter must be one of: none, vertical, horizontal, trisect, or quarter.",

              "setSplitType (none|horizontal|vertical|trisect|quarter)");
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
                switch(args.get(0)) {
                    case "none"       -> XJGE.setScreenSplit(Split.NONE);
                    case "horizontal" -> XJGE.setScreenSplit(Split.HORIZONTAL);
                    case "vertical"   -> XJGE.setScreenSplit(Split.VERTICAL);
                    case "trisect"    -> XJGE.setScreenSplit(Split.TRISECT);
                    case "quarter"    -> XJGE.setScreenSplit(Split.QUARTER);

                    default -> setOutput(errorInvalidArg(args.get(0), "(none), (horizontal), (vertical), (trisect), or (quarter)"), Color.YELLOW);
                }
            }
        }
    }

}