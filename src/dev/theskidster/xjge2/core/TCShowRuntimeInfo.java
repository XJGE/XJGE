package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class TCShowRuntimeInfo extends TerminalCommand {

    public TCShowRuntimeInfo() {
        super("Provides information pertaining to the current state of the engine at runtime.", 

              useGenericShowing("component visibility"),

              "showRuntimeInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Game.setRuntimeInfoVisible(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.YELLOW);
            }
        } else {
            Game.setRuntimeInfoVisible(!Game.getRuntimeInfoVisible());
        }
    }

}