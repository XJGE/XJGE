package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCFindMonitors extends TerminalCommand {

    public TCFindMonitors() {
        super("Updates the internally managed list of monitors available for use " +
              "by the application. WARNING: calling this may invalidate existing " +
              "references. In this instance, the engine will attempt to gracefully " + 
              "recover the window and reassign it using a new reference.", 
              
              "Simply type findMonitors to use. This command contains no parameters.", 
              
              "findMonitors");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        Window.findMonitors();
        return new TerminalOutput("Monitor list updated", Color.WHITE);
    }

}