package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

//Created: May 25, 2021

/**
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
    public void execute(List<String> args) {
        setOutput("cya!", Color.WHITE);
        glfwSetWindowShouldClose(Window.HANDLE, true);
    }

}