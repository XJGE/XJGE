package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 26, 2021
 * <p>
 * Lists all commands available to the command terminal. This includes 
 * user-defined commands.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCListCommands extends TerminalCommand {
    
    /**
     * Creates a new instance of the listCommands command.
     */
    TCListCommands() {
        super("Lists every command available to the terminal including those " +
              "provided by users of the public API.", 
              
              "Simply type listCommands to use. This command contains no parameters.", 
              
              "showCommands");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        String commandList = "";
        commandList = commands.keySet().stream()
                                       .map(name -> name + ", ")
                                       .reduce(commandList, String::concat);

        return new TerminalOutput(commandList.substring(0, commandList.length() - 2), Color.CYAN);
    }

}