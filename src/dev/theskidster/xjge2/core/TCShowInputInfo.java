package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Jun 8, 2021
 */

class TCShowInputInfo extends TerminalCommand {

    TCShowInputInfo() {
        super("Provides information regarding connected input devices.", 
                
              useGenericShowing("component visibility"), 
              
              "showInputInfo [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Game.setInputInfoVisible(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.YELLOW);
            }
        } else {
            Game.setInputInfoVisible(!Game.getInputInfoVisible());
        }
    }

}