package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Jun 8, 2021
 */

final class TCShowSystemInfo extends TerminalCommand {

    TCShowSystemInfo() {
        super("Provides information about the architecture on which the engine is currently running.", 

              useGenericShowing("component visibility"),

              "showSystemInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Game.setSystemInfoVisible(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.YELLOW);
            }
        } else {
            Game.setSystemInfoVisible(!Game.getSystemInfoVisible());
        }
    }
    
}