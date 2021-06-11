package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCShowCommands extends TerminalCommand {
    
    TCShowCommands() {
        super("Shows all commands available to the terminal.", 
              
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

        setOutput(commandList.substring(0, commandList.length() - 2), Color.PINK);
    }

}