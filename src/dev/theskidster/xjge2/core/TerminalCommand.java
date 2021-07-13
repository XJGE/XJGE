package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

public abstract class TerminalCommand {

    private final String description;
    private final String usage;
    private final String syntax;
    
    protected final Color angryRed = Color.RED;
    
    protected TerminalOutput output;
    
    protected Map<String, TerminalCommand> commands;
    
    public TerminalCommand(String description, String usage, String syntax) {
        this.description = description;
        this.usage       = usage;
        this.syntax      = syntax;
    }
    
    public abstract void execute(List<String> args);
    
    String getDescription() {
        return description;
    }
    
    String getUsage() {
        return usage;
    }
    
    String getSyntax() {
        return syntax;
    }
    
    TerminalOutput getOutput() {
        return output;
    }
    
    protected void setOutput(String text, Color color) {
        output = new TerminalOutput(text + "\n", color);
    }
    
    protected void setCommands(Map<String, TerminalCommand> commands) {
        this.commands = Collections.unmodifiableMap(commands);
    }
    
    public static String useGenericSetter(String diff) {
        return "Supplying prev or next will iterate backwards or forwards through each " + 
               "available " + diff + " whereas passing a number will explicitly set the " + 
                diff + " to whichever currently occupies that ID (if it exists).";
    }
    
    public static String useGenericShowing(String diff) {
        return "Passing no argument will toggle " + diff + ". Supplying either true or " + 
                "false will set it explicitly.";
    }
    
    protected String errorNotEnoughArgs(int required) {
        return "ERROR: Invalid number of arguments. Required at least " + required + ".";
    }
    
    protected String errorTooManyArgs(int numPassed, int maxArgs) {
        return "ERROR: Invalid number of arguments. Found " + numPassed + ", max of " + maxArgs + " allowed.";
    }
    
    protected String errorInvalidArg(String found, String required) {
        return "ERROR: Invalid argument. Found " + found + ", required " + required + ".";
    }
    
}