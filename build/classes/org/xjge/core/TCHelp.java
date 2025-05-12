package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 26, 2021
 * <p>
 * Provides users with information about commands and the command terminal.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCHelp extends TerminalCommand {
    
    /**
     * Creates a new instance of the help command.
     */
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
                
                setOutput(info, Color.GREEN);
            }

            case 1 -> {
                command = args.get(0);

                if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                    setOutput(command + " - " + commands.get(command).getDescription(), Color.CYAN);
                } else {
                    setOutput(commandNotFound(command), Color.RED);
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
                            setOutput(commandNotFound(command), Color.RED);
                        }
                    }
                    
                    case "-s" -> {
                        if(commands.keySet().stream().anyMatch(name -> name.equals(command))) {
                            setOutput(commands.get(command).getSyntax(), Color.YELLOW);
                        } else {
                            setOutput(commandNotFound(command), Color.RED);
                        }
                    }

                    default -> setOutput("ERROR: Unknown wildcard: \"" + wildcard + "\", use -u or -s.", Color.RED);
                }
            }

            default -> setOutput(errorTooManyArgs(args.size(), 2), Color.RED);
        }
    }
    
    /**
     * Generates an error message whenever a command of the name specified 
     * cannot be found.
     * 
     * @param command the name of the command that doesn't exist
     * 
     * @return a generic string indicating an error
     */
    private String commandNotFound(String command) {
        return "ERROR: Unable to find command: \"" + command + "\".";
    }

}