package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 26, 2021
 * <p>
 * Shows all commands available to the command terminal. This includes 
 * user-defined commands.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCShowCommands extends TerminalCommand {
    
    /**
     * Creates a new instance of the showCommands command.
     */
    TCShowCommands() {
        super("Shows all commands available to the command terminal.", 
              
              "Simply type showCommands to use. This command contains no " +
              "additional parameters.", 
              
              "showCommands");
    }

    @Override
    public void execute(List<String> args) {
        String commandList = "";
        commandList = commands.keySet()
                                    .stream()
                                    .map(name -> name + ", ")
                                    .reduce(commandList, String::concat);

        setOutput(commandList.substring(0, commandList.length() - 2), Color.CYAN);
    }

}