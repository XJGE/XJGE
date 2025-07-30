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
final class TCSetVSyncEnabled extends TerminalCommand {

    /**
     * Creates a new instance of the setVSync command.
     */
    TCSetVSyncEnabled() {
        super("Changes whether or not vertical sync is enabled.",

              useGenericShowing("vsync"),

              "setVSyncEnabled [true|false]");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                Window.setVSyncEnabled(value);
                return new TerminalOutput("VSync changed: (" + value + ")", Color.WHITE);
            } else {
                return new TerminalOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            Window.setVSyncEnabled(!Window.getVSyncEnabled());
            return new TerminalOutput("VSync changed: (" + Window.getVSyncEnabled() + ")", Color.WHITE);
        }
    }

}