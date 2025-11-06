package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCListMonitors extends TerminalCommand {

    public TCListMonitors() {
        super("Provides a list of every connected display device available for " + 
              "use by the application.", 
              
              "Simply type listMonitors to use. This command contains no parameters.", 
              
              "listMonitors");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        var monitorList = new StringBuilder();
        
        for(int i = 0; i < Window.getNumMonitors(); i++) {
            var monitor = Window.getMonitor(i);
            
            monitorList.append("\"").append(monitor.name).append("\" (")
                       .append(monitor.getInfo()).append(") at index ").append(i);
            
            if(i < Window.getNumMonitors() - 1) monitorList.append(" \n");
        }
        
        return new TerminalOutput(monitorList.toString(), Color.YELLOW);
    }

}