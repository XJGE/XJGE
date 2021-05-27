package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCShowCommands extends TerminalCommand {

    private final Terminal terminal;
    
    public TCShowCommands(Terminal terminal) {
        super("Shows all commands available to the terminal.", 
              
              "Simply type showCommands to use. This command contains no " +
              "additional parameters.", 
              
              "showCommands");
        
        this.terminal = terminal;
    }

    @Override
    public void execute(List<String> args) {
        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.YELLOW);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.YELLOW);
            } else {
                String commandList = "";
                commandList = terminal.commands.keySet()
                                               .stream()
                                               .map(name -> name + ", ")
                                               .reduce(commandList, String::concat);
                
                setOutput(commandList, Color.CYAN);
            }
        }
    }

}