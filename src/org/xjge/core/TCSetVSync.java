package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 26, 2021
 * <p>
 * Changes whether or not vertical sync is enabled.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetVSync extends TerminalCommand {

    /**
     * Creates a new instance of the setVSync command.
     */
    TCSetVSync() {
        super("Changes whether or not vertical sync is enabled.",

              useGenericShowing("vsync"),

              "setVSync [true|false]");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Hardware.setVSyncEnabled(value);
                return new TerminalOutput("VSync changed: (" + value + ")", Color.WHITE);
            } else {
                return new TerminalOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            Hardware.setVSyncEnabled(!Hardware.getVSyncEnabled());
            return new TerminalOutput("VSync changed: (" + Hardware.getVSyncEnabled() + ")", Color.WHITE);
        }
    }

}