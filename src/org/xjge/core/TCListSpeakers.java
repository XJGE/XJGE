package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCListSpeakers extends TerminalCommand {

    public TCListSpeakers() {
        super("Provides a list of every connected audio device available for " + 
              "use by the application.", 
              
              "Simply type listSpeakers to use. This command contains no parameters.", 
              
              "listSpeakers");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        var speakerList = new StringBuilder();
        
        for(int i = 0; i < Audio2.getNumSpeakers(); i++) {
            var speaker = Audio2.getSpeaker(i);
            
            speakerList.append("\"").append(speaker.name).append("\" at index ").append(i);
            
            if(i < Audio2.getNumSpeakers() - 1) speakerList.append(" \n");
        }
        
        return new TerminalOutput(speakerList.toString(), Color.YELLOW);
    }

}