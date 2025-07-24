package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCRunGarbageCollection extends TerminalCommand {

    public TCRunGarbageCollection() {
        super("Forces a garbage collection cycle to free unused memory. " + 
              "Useful for testing memory usage and performance during development.", 

              "Simply type runGarbageCollection to use. This command contains no parameters.",

              "runGarbageCollection");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        System.gc();
        
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double reclaimed = (memoryBefore - memoryAfter) / (1024.0 * 1024.0);
        
        String message = reclaimed >= 0
                       ? String.format("Reclaimed %.2f MB of heap memory.", reclaimed)
                       : String.format("Heap usage increased by %.2f MB (heap resized).", Math.abs(reclaimed));
        
        return new TerminalOutput(message, Color.WHITE);
    }

}