package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

final class TCHelp extends TerminalCommand {
    
    TCHelp() {
        super("Supplies information about the terminal and its various " + 
              "commands. Use -u, or -s, prior to the commands name to view " + 
              "its usage or syntax.",

              "Using help without arguments will provide basic information " + 
              "about the command terminal. Passing a command name will provide " + 
              "a description of that command. Use -u, or -s, to prior to the " + 
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
                              "TAB to autocomplete, ENTER to execute, and SHIFT+F1 " + 
                              "to exit the terminal. A full list of commands can " + 
                              "be viewed by using showCommands.";
                
                setOutput(info, Color.LIME);
            }

            case 1 -> {
                command = args.get(0);

                if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                    setOutput(command + " - " + commands.get(command).getDescription(), Color.CYAN);
                } else {
                    setOutput(commandNotFound(command), angryRed);
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
                            setOutput(commandNotFound(command), Color.ORANGE);
                        }
                    }
                    
                    case "-s" -> {
                        if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(commands.get(command).getSyntax(), Color.YELLOW);
                        } else {
                            setOutput(commandNotFound(command), Color.ORANGE);
                        }
                    }

                    default -> setOutput("ERROR: Unknown wildcard: \"" + wildcard + "\", use -u or -s.", Color.ORANGE);
                }
            }

            default -> setOutput(errorTooManyArgs(args.size(), 2), Color.ORANGE);
        }
    }
    
    private String commandNotFound(String command) {
        return "ERROR: Unable to find command: \"" + command + "\".";
    }

}