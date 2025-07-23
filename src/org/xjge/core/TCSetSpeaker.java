package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: Jun 24, 2021
 * <p>
 * Changes the current audio output device.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetSpeaker extends TerminalCommand {

    /**
     * Creates a new instance of the setSpeaker command.
     */
    TCSetSpeaker() {
        super("Changes the current audio output device.", 

              useGenericSetter("device"),

              "setSpeaker (next|prev|<int>)");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        output = null;

        if(!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));

                if(value > -1 && value < Hardware.getNumSpeakers()) {
                    Hardware.setSpeaker(args.get(0));
                    return new TerminalOutput("Set current audio device to " + Audio.speaker.id + 
                              " \"" + Audio.speaker.name.substring(15) + "\"", 
                              Color.WHITE);
                } else {
                    return new TerminalOutput("ERROR: Could not find an audio device by the ID of " + value, Color.RED);
                }
            } catch(NumberFormatException e) {
                if(args.get(0).equals("next") || args.get(0).equals("prev")) {
                    Hardware.setSpeaker(args.get(0));
                    return new TerminalOutput("Set current audio device to " + Audio.speaker.id + 
                              " \"" + Audio.speaker.name.substring(15) + "\"", 
                              Color.WHITE);
                } else {
                    return new TerminalOutput(errorInvalidArg(args.get(0), "<int>, (next), or (prev)"), Color.RED);
                }
            }
        } else {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }

}