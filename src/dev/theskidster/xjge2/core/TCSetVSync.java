package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCSetVSync extends TerminalCommand {

    TCSetVSync() {
        super("Changes whether or not vertical sync is enabled.",

              useGenericShowing("vsync"),

              "setVSync [true|false]");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                setOutput("VSync changed: (" + value + ")", Color.WHITE);
                WinKit.setVSyncEnabled(value);
            } else {
                setOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.YELLOW);
            }
        } else {
            WinKit.setVSyncEnabled(!WinKit.getVSyncEnabled());
            setOutput("VSync changed: (" + WinKit.getVSyncEnabled() + ")", Color.WHITE);
        }
    }

}