package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * @author J Hoffman
 * Created: May 25, 2021
 */

/**
 * Ceases execution and gracefully exits the application.
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