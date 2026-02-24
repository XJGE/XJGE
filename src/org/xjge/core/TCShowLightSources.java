package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCShowLightSources extends TerminalCommand {

    TCShowLightSources() {
        super("Changes whether or not vertical sync is enabled.",

              useGenericShowing("light source visibility"),

              "showLightSources [true|false]");
    }
    
    @Override
    public TerminalOutput execute(List<String> args) {
        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                LightingSystem.showLightSources = value;
                return new TerminalOutput("Light source visibility changed: (" + value + ")", Color.WHITE);
            } else {
                return new TerminalOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            LightingSystem.showLightSources = !LightingSystem.showLightSources;
            return new TerminalOutput("Light source visibility changed: (" + Window.getVSyncEnabled() + ")", Color.WHITE);
        }
    }

}