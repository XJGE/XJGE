package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCHelp extends TerminalCommand {
    
    public TCHelp() {
        super("Supplies information about commands.",

              "Using help without arguments will provide basic information " + 
              "about the command terminal. Passing a command name will provide " + 
              "a description of that command. Use -u or -s to prior to the " + 
              "command name to view its usage or syntax.",

              "help [-u|-s], [<command name>]");
    }

    @Override
    public void execute(List<String> args) {
        String wildcard;
        String command;

        switch(args.size()) {
            case 0 -> {
                String info = "Start typing to view command suggestions, press " + 
                              "TAB to autocomplete, ENTER to execute, and F1 to " + 
                              "exit the terminal. A full list of commands can " + 
                              "be viewed by using showCommands.";
                
                setOutput(info, Color.LIME);
            }

            case 1 -> {
                command = args.get(0);

                if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                    setOutput(command + " - " + commands.get(command).getDescription(), Color.WHITE);
                } else {
                    setOutput(commandNotFound(command), Color.YELLOW);
                }
            }

            case 2 -> {
                wildcard = args.get(0);
                command  = args.get(1);

                switch(wildcard) {
                    case "-u" -> {
                        if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(commands.get(command).getUsage(), Color.WHITE);
                        } else {
                            setOutput(commandNotFound(command), Color.YELLOW);
                        }
                    }
                    
                    case "-s" -> {
                        if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(commands.get(command).getSyntax(), Color.YELLOW);
                        } else {
                            setOutput(commandNotFound(command), Color.YELLOW);
                        }
                    }

                    default -> setOutput("ERROR: Unknown wildcard: \"" + wildcard + "\", use -u or -s.", Color.YELLOW);
                }
            }

            default -> setOutput(errorTooManyArgs(args.size(), 2), Color.YELLOW);
        }
    }
    
    private String commandNotFound(String command) {
        return "ERROR: Unable to find command: \"" + command + "\".";
    }

}