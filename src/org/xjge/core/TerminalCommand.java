package org.xjge.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created: May 25, 2021
 * <p>
 * Abstract class used to define additional commands for the {@link Terminal}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class TerminalCommand {

    private final String description;
    private final String usage;
    private final String syntax;
    
    protected Map<String, TerminalCommand> commands;
    
    /**
     * Creates a new terminal command with metadata that the 
     * {@linkplain TCHelp help command} will display.
     * <table border="1"><caption>On Syntax Structure:</caption>
     * <tr><td><b>Syntax</b></td><td><b>Meaning</b></td></tr>
     * <tr><td>plain text</td><td>Enter this literally, exactly as shown.</td></tr>
     * <tr><td>&lt;type&gt;</td><td>An argument that should be replaced with an 
     * appropriate value of the specified data type.</td></tr>
     * <tr><td>[entry]</td><td>This entry is optional.</td></tr>
     * <tr><td>(entry|entry)</td><td>(Required) Pick one of the entries that 
     * are shown.</td></tr>
     * <tr><td>[entry|entry]</td><td>(Optional) Pick one of the entries that 
     * are shown.</td></tr>
     * </table>
     * 
     * @param description a description of what the command does
     * @param usage a description of how to use the command from the terminal
     * @param syntax the syntax structure of the command
     */
    public TerminalCommand(String description, String usage, String syntax) {
        this.description = description;
        this.usage       = usage;
        this.syntax      = syntax;
    }
    
    /**
     * Executes the command and creates an output string indicating its success 
     * or failure. Values parsed from the arguments passed by the command 
     * terminal must be validated individually by subclasses that contain this 
     * method.
     * 
     * @param args any arguments required by the command
     */
    public abstract TerminalOutput execute(List<String> args);
    
    /**
     * Obtains a description of what the command does.
     * 
     * @return the commands description as it appears in its constructor
     */
    String getDescription() {
        return description;
    }
    
    /**
     * Obtains a description of how to use the command in the terminal.
     * 
     * @return the commands usage as it appears in its constructor
     */
    String getUsage() {
        return usage;
    }
    
    /**
     * Obtains the syntax structure of the command.
     * 
     * @return the commands syntax structure as it appears in its constructor
     */
    String getSyntax() {
        return syntax;
    }
    
    /**
     * Obtains a copy of a collection of every command the terminal will 
     * support. This is used by the engine by commands like 
     * {@linkplain TCShowCommands showCommmands} and {@linkplain TCHelp help}.
     * 
     * @param commands the commands the terminal will support
     */
    void setCommands(Map<String, TerminalCommand> commands) {
        this.commands = Collections.unmodifiableMap(commands);
    }
    
    /**
     * Provides a generic response for "setter" style commands with a similar 
     * signature. Commands are considered "setter" style if they can be used to 
     * iterate and apply changes through some collection.
     * 
     * @param diff the small difference used to better contextualize the response
     * 
     * @return the generated message as a string
     */
    protected static String useGenericSetter(String diff) {
        return "Supplying prev or next will iterate backwards or forwards through each " + 
               "available " + diff + " whereas passing a number will explicitly set the " + 
                diff + " to whichever currently occupies that ID (if it exists).";
    }
    
    /**
     * Provides a generic response for "showing" style commands with a similar 
     * signature. Commands are considered "showing" style if they control some 
     * boolean value.
     * 
     * @param diff the small difference used to better contextualize the response
     * 
     * @return the generated message as a string
     */
    protected static String useGenericShowing(String diff) {
        return "Passing no argument will toggle " + diff + ". Supplying either true or " + 
                "false will set it explicitly.";
    }
    
    /**
     * Provides a generic response for when users supply an insufficient amount 
     * of arguments to a command.
     * 
     * @param required the minimum number of arguments required by this command
     * 
     * @return an error message as a string that can be passed to 
     *         {@link setOutput setOutput()}
     */
    protected String errorNotEnoughArgs(int required) {
        return "ERROR: Invalid number of arguments. Required at least " + required + ".";
    }
    
    /**
     * Provides a generic response for when users supply more arguments than 
     * allowed by a command.
     * 
     * @param numPassed the number of arguments provided by the user
     * @param maxArgs the maximum number of arguments allowed by the command
     * 
     * @return an error message as a string that can be passed to 
     *         {@link setOutput setOutput()}
     */
    protected String errorTooManyArgs(int numPassed, int maxArgs) {
        return "ERROR: Invalid number of arguments. Found " + numPassed + ", max of " + maxArgs + " allowed.";
    }
    
    /**
     * Provides a generic response for when users supply an argument to a 
     * command which cannot be processed either due to differing data types or 
     * a syntax error.
     * 
     * @param found the argument provided by the user
     * @param required the argument(s) or data types as accepted by the command
     * 
     * @return an error message as a string that can be passed to 
     *         {@link setOutput setOutput()}
     */
    protected String errorInvalidArg(String found, String required) {
        return "ERROR: Invalid argument. Found " + found + ", required " + required + ".";
    }
    
}