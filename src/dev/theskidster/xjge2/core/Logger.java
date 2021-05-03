package dev.theskidster.xjge2.core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public final class Logger {
    
    private static String domain = "";
    
    private static PrintWriter logText;
    private static final StringBuilder output = new StringBuilder();
    
    private static void horizontalLine() {
        String line = "--------------------------------------------------------------------------------";
        System.out.println(line);
        output.append(line)
              .append(System.lineSeparator());
    }
    
    static void printSystemInfo() {
        horizontalLine();
        logInfo("OS NAME:\t\t" + System.getProperty("os.name"));
        logInfo("JAVA VER:\t\t" + System.getProperty("java.version"));
        logInfo("GLFW VER:\t\t" + glfwGetVersionString());
        logInfo("OPENAL VER:\t" );
        logInfo("OPENGL VER:\t" + glGetString(GL_VERSION));
        logInfo("MONITORS:\t\t" + "Found: " + WinKit.getNumMonitors() + 
                ", Primary: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")");
        logInfo("SPEAKERS:\t\t" );
        logInfo("GAMEPADS:\t\t" );
        horizontalLine();
    }
    
    public static void newLine() {
        System.out.println();
        output.append(System.lineSeparator());
    }
    
    public static void setDomain(String domain) {
        Logger.domain = (domain != null) ? " (" + domain + ")" : "";
    }
    
    public static void logInfo(String message) {
        System.out.println("INFO" + domain + ": " + message);
        
        output.append("INFO")
              .append(domain)
              .append(": ")
              .append(message)
              .append(System.lineSeparator());
    }
    
    public static void logWarning(String message, Exception e) {
        String timestamp = new SimpleDateFormat("MM-dd-yyyy h:mma").format(new Date());
        
        System.out.println(System.lineSeparator() + timestamp);
        System.out.println("WARNING" + domain + ": " + message + System.lineSeparator());
        
        output.append(System.lineSeparator())
              .append(timestamp)
              .append(System.lineSeparator())
              .append("WARNING")
              .append(domain)
              .append(": ")
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
    
    public static void logSevere(String message, Exception e) {
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        String time = new SimpleDateFormat("h:mma").format(new Date());
        
        String timestamp = date + " " + time;
                
        System.err.println(System.lineSeparator() + timestamp);
        System.err.println("ERROR" + domain + ": " + message + System.lineSeparator());

        output.append(System.lineSeparator())
              .append(timestamp)
              .append(System.lineSeparator())
              .append("ERROR")
              .append(domain)
              .append(": ")
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
        
        File file     = new File("log " + date + ".txt");
        int duplicate = 0;

        while(file.exists()) {
            duplicate++;
            file = new File("log " + date + " (" + duplicate + ").txt");
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