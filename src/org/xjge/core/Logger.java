package org.xjge.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * Created: Apr 28, 2021
 * <br><br>
 * Used to keep a chronological record of significant events occurring within 
 * the application during runtime. The logger provides the following methods for 
 * generating text output:
 * <ul>
 * <li>{@linkplain logInfo(String) logInfo()} - Writes a low-priority message, 
 *     useful for tracking state changes</li>
 * <li>{@linkplain logWarning(String, Exception) logWarning()} - Indicates that the 
 *     application may have encountered a non-fatal error</li>
 * <li>{@linkplain logError(String, Exception) logSevere()} - A fatal error has 
 *     occurred which requires the application to terminate execution</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Logger {

    private static final StringBuilder output = new StringBuilder();
    
    /**
     * Outputs messages to the applications console and log file using various
     * levels of severity.
     * 
     * @param level the level of severity (either INFO, WARNING, or ERROR)
     * @param message the text to appear as output in the console and log file
     * @param exception an optional argument that will output the provided stack 
     *                  trace
     */
    private static void log(String level, String message, Exception exception) {
        String date = new SimpleDateFormat("MMM dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ssa").format(new Date());
        
        String timestamp = date + ", " + time.toLowerCase();
        String filename  = Thread.currentThread().getStackTrace()[3].getFileName();
        
        String info = timestamp + ", [" + level + "], [" + filename + "]: " + message;
        
        if(level.equals("ERROR")) {
            System.err.println(info);
            if(exception == null) exception = new RuntimeException();
        } else {
            System.out.println(info);
        }
        
        output.append(info).append(System.lineSeparator());
        
        if(exception != null) {
            System.out.println(exception.toString());
            output.append(exception.toString()).append(System.lineSeparator());
            
            for(StackTraceElement element : exception.getStackTrace()) {
                System.out.println("\t" + element.toString());

                output.append("\t")
                      .append(element.toString())
                      .append(System.lineSeparator());
            }
        }
    }
    
    /**
     * Outputs information pertaining to the system on which the application is
     * currently running.
     */
    static void logSystemInfo() {
        for(int i = 0; i < 14; i++) {
            String info = switch(i) {
                default -> "****************************************************************************************************";        
                case 1  -> " OS NAME:\t" + System.getProperty("os.name");
                case 2  -> " JAVA VER:\t" + System.getProperty("java.version");
                case 3  -> " XJGE VER:\t" + XJGE.VERSION;
                case 4  -> " GLFW VER:\t" + glfwGetVersionString();
                case 5  -> " OPENAL VER:\t" + alGetString(AL_VERSION);
                case 6  -> " OPENGL VER:\t" + glGetString(GL_VERSION);
                case 8  -> " CPU MODEL:\t" + XJGE.getCPUModel();
                case 9  -> " GPU MODEL:\t" + glGetString(GL_RENDERER);
                case 10 -> " MONITORS:\t" + "Found: " + Window.getNumMonitors() + ", Primary: \"" + 
                           Window.getMonitor().name + "\" (" + Window.getMonitor().getInfo() + ")";
                case 11 -> " SPEAKERS:\t" + "Found: " + Audio.getSpeakerCount() + ", Primary: \"" + Audio.getSpeaker().name + "\"";
                case 12 -> " GAMEPADS:\t" + "Found: " + Input.getGamepadCount();
            };
            
            System.out.println(info);
            output.append(info).append(System.lineSeparator());
        }
        
        System.out.println();
        output.append(System.lineSeparator());
    }
    
    /**
     * Writes a low-severity message to the applications console and log file. 
     * This is used to indicate significant state changes that have occurred 
     * during runtime.
     * 
     * @param message the text to appear as output in the console and log file
     */
    public static void logInfo(String message) {
        log("INFO", message, null);
    }
    
    /**
     * Writes a medium-severity message to the applications console and log 
     * file. Warning messages are used in cases where the application has 
     * entered an unexpected state or to indicate that the public API is being 
     * misused. The application will likely still run for the time being but may 
     * exhibit undefined behavior following a warning.
     * 
     * @param message the text to appear as output in the console and log file
     * @param exception an optional argument that will output the provided stack 
     *                  trace, or nothing if <b>null</b> is passed
     */
    public static void logWarning(String message, Exception exception) {
        log("WARNING", message, exception);
    }
    
    /**
     * Writes a high-severity message to the applications console and log file.
     * This is used in extreme cases where the application has encountered some
     * fatal error that requires it to stop execution.
     * 
     * @param message the text to appear as output in the console and log file
     * @param exception an optional argument that will output the provided stack 
     *                  trace, or a generic {@link RuntimeException} if 
     *                  <b>null</b> is passed
     */
    public static void logError(String message, Exception exception) {
        log("ERROR", message, exception);
        
        String fileDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        File file       = new File("log-" + fileDate + ".txt");
        int duplicate   = 0;
        
        while(file.exists()) {
            duplicate++;
            file = new File("log-" + fileDate + " (" + duplicate + ").txt");
        }
        
        //TODO: output error message/crash report to user
        
        try(PrintWriter logText = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file.getName()), StandardCharsets.UTF_8))) {
            int maxSize = 3 * 1024 * 1024; //Roughly 3mb
            
            String text = (output.toString().getBytes(StandardCharsets.UTF_8).length >= maxSize) 
                        ? output.substring(Math.max(output.length() - maxSize, 0)) 
                        : output.toString();
            
            logText.append(text);
        } catch(Exception fileException) {
            System.err.println(fileException);
        }
        
        System.exit(-1);
    }
    
}