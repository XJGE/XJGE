package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: May 25, 2021
 * <p>
 * Ceases execution and gracefully exits the application.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCTerminate extends TerminalCommand {

    /**
     * Creates a new instance of the terminate command.
     */
    TCTerminate() {
        super("Ceases execution and gracefully exits the application.",

              "Simply type terminate to use. This command contains no " +
              "additional parameters.", 

              "terminate");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        Window.close();
        return new TerminalOutput("cya!", Color.WHITE);
    }

}