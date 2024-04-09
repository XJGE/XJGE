package org.xjge.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

//Created: Apr 28, 2021

/**
 * A lightweight application logger comprised of a single static class which 
 * can be used to keep a chronological record of significant events occurring 
 * within the application at runtime.
 * <p>
 * Output provided by the logger can be viewed through the applications console 
 * at runtime, or after the application has ceased execution following a call 
 * made to {@link logSevere(String, Exception) logSevere()} in which case a 
 * .txt file will be generated containing the loggers output.
 * <p>
 * The logger provides the following methods for generating text output:
 * <ul>
 * <li>{@link logInfo(String) logInfo()} - Produces a low-priority message, 
 *     useful for tracking state changes.</li>
 * <li>{@link logWarning(String, Exception) logWarning()} - Indicates that the 
 *     application may have encountered a non-fatal error.</li>
 * <li>{@link logSevere(String, Exception) logSevere()} - A fatal error has 
 *     occurred which will require the application to cease execution 
 *     immediately.</li>
 * <li>{@link setDomain(String) setDomain()} - Appends the name of whichever 
 *     application is using the logger to the log messages it 
 *     creates.</li>
 * <li>{@link newLine() newLine()} - Inserts a new line into the text 
 *     output.</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Logger {
    
    //TODO: add restriction to log file size.
    
    private static String domain = "";
    
    private static PrintWriter logText;
    private static final StringBuilder output = new StringBuilder();
    
    /**
     * Inserts a horizontal line into the logger output with no other 
     * information.
     */
    private static void horizontalLine() {
        String line = "**************************************************" +
                      "**************************************************";
        System.out.println(line);
        output.append(line)
              .append(System.lineSeparator());
    }
    
    /**
     * Outputs information pertaining to the environment variable of the system 
     * on which the engine is currently running.
     */
    static void printSystemInfo() {
        horizontalLine();
        logUnspecified("OS NAME:\t" + System.getProperty("os.name"));
        logUnspecified("JAVA VER:\t" + System.getProperty("java.version"));
        logUnspecified("XJGE VER:\t" + XJGE.VERSION);
        logUnspecified("GLFW VER:\t" + glfwGetVersionString());
        logUnspecified("OPENAL VER:\t" + alGetString(AL_VERSION));
        logUnspecified("OPENGL VER:\t" + glGetString(GL_VERSION));
        logUnspecified("GFX CARD:\t" + glGetString(GL_RENDERER));
        logUnspecified("MONITORS:\t" + "Found: " + Hardware.getNumMonitors() + 
                       ", Primary: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")");
        logUnspecified("SPEAKERS:\t" + "Found: " + Hardware.getNumSpeakers() + 
                       ", Primary: \"" + Audio.speaker.name.substring(15));
        logUnspecified("GAMEPADS:\t" + "Found: " + Input.getNumDevices());
        horizontalLine();
        newLine();
    }
    
    /**
     * Inserts an empty space into the logger output with no other information. 
     * Included here to encourage structure.
     */
    static void newLine() {
        System.out.println();
        output.append(System.lineSeparator());
    }
    
    /**
     * Writes text to the applications console and log file with no timestamp, 
     * severity, or domain. Used internally by the engine.
     * 
     * @param message the text to appear as output in the console/log file 
     */
    static void logUnspecified(String message) {
        System.out.println(message);
        
        output.append(message)
              .append(System.lineSeparator());
    }
    
    /**
     * Appends the name of a domain to the log message following the priority 
     * indicator (INFO, WARNING, etc). Included so log messages can be better 
     * located within different domains of the engine. You likely won't need to 
     * make use of it in your game project unless you really want to.
     * <p>
     * NOTE: Should you choose to utilize this feature remember to pass 
     * <b>null</b> following the log message to reset the state of the logger 
     * so any log messages following this one do not inadvertently exhibit 
     * erroneous domain names. This process often looks something like this:
     * 
     * <blockquote><pre>
     * setDomain("myDomain");
     *     logInfo(...);
     * setDomain(null);
     * </pre></blockquote>
     * 
     * @param domain the name of the domain to append to the log message
     */
    public static void setDomain(String domain) {
        Logger.domain = (domain != null) ? domain : "project";
    }
    
    /**
     * Writes an informative low-priority message to the applications console. 
     * Typically used to indicate significant state changes occurring within 
     * the application.
     * 
     * @param message the text to appear as output in the console/log file 
     */
    public static void logInfo(String message) {
        String date = new SimpleDateFormat("MMM dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ssa").format(new Date());
        
        String timestamp = date + ", " + time.toLowerCase();
        
        System.out.println(timestamp + ", [INFO], [" + domain + "]: " + message);
        
        output.append(timestamp)
              .append(", [INFO], [")
              .append(domain)
              .append("]: ")
              .append(message)
              .append(System.lineSeparator());
    }
    
    /**
     * Writes a medium-priority message to the applications console. Warning 
     * messages should be used to indicate that the application may have 
     * entered an invalid state which hasn't (yet) resulted in a crash- but may 
     * produce undefined behavior.
     * 
     * @param message the text to appear as output in the console/log file 
     * @param e       an optional exception used to output a stack trace. If 
     *                {@code null} is passed, no stack trace information will 
     *                be displayed.
     */
    public static void logWarning(String message, Exception e) {
        String date = new SimpleDateFormat("MMM dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ssa").format(new Date());
        
        String timestamp = date + ", " + time.toLowerCase();
        
        System.out.println(timestamp + ", [WARNING], [" + domain + "]: " + message + System.lineSeparator());
        
        output.append(timestamp)
              .append(", [WARNING], [")
              .append(domain)
              .append("]: ")
              .append(message)
              .append(System.lineSeparator())
              .append(System.lineSeparator());
        
        //Output the stack trace if an exception if it's provided.
        if(e != null) {
            var stackTrace = e.getStackTrace();
            
            System.out.println(e.toString());
            output.append(e.toString())
                  .append(System.lineSeparator());
            
            for(StackTraceElement element : stackTrace) {
                System.out.println("\t" + element.toString());
                
                output.append("\t")
                      .append(element.toString())
                      .append(System.lineSeparator());
            }
            
            System.out.println();
            output.append(System.lineSeparator());
        }
    }
    
    /**
     * Writes a high-priority message to the applications console. Use of this
     * method is reserved only for instances wherein the application has 
     * encountered some fatal error that will require it to cease execution.
     * <p>
     * A .txt file containing the recorded output will be generated in the 
     * directory from which the application was launched.
     * 
     * @param message the text to appear as output in the console/log file 
     * @param e       an optional exception used to output a stack trace. If 
     *                {@code null} is passed, JLogger will generate a 
     *                nondescript {@link RuntimeException}.
     */
    public static void logSevere(String message, Exception e) {
        String date = new SimpleDateFormat("MMM dd").format(new Date());
        String time = new SimpleDateFormat("hh:mm:ssa").format(new Date());
        
        String timestamp = date + ", " + time.toLowerCase();
        
        System.err.println(timestamp + ", [ERROR], [" + domain + "]: " + message + System.lineSeparator());

        output.append(timestamp)
              .append(", [ERROR], [")
              .append(domain)
              .append("]: ")
              .append(message)
              .append(System.lineSeparator())
              .append(System.lineSeparator());

        //If no exception is provided we'll throw our own.
        if(e == null) e = new RuntimeException();
        var stackTrace = e.getStackTrace();
        
        System.err.println(e.toString());
        output.append(e.toString())
              .append(System.lineSeparator());

        for(StackTraceElement element : stackTrace) {
            System.err.println("\t" + element.toString());

            output.append("\t")
                  .append(element.toString())
                  .append(System.lineSeparator());
        }

        System.err.println();
        output.append(System.lineSeparator());
        
        String fileDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        
        File file = new File("log-" + fileDate + ".txt");
        int duplicate = 0;
        
        while(file.exists()) {
            duplicate++;
            file = new File("log-" + fileDate + " (" + duplicate + ").txt");
        }

        try(FileWriter logFile = new FileWriter(file.getName())) {
            //Check size make sure we dont produce a crazy large file.
            String text = (output.toString().length() >= Integer.MAX_VALUE) 
                        ? output.substring(0, Integer.MAX_VALUE) 
                        : output.toString();
            
            logText = new PrintWriter(logFile);
            logText.append(text);
            logText.close();
        } catch(Exception ex) {}
        
        System.exit(-1);
    }
    
}