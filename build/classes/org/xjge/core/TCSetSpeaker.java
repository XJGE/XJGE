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
        if(!args.isEmpty()) {
            try {
                int index   = Integer.parseInt(args.get(0));
                var speaker = AudioSystem.getSpeaker(index);
                
                if(AudioSystem.setSpeaker(speaker)) {
                    return new TerminalOutput("Changed current audio device to \"" + speaker.name + 
                                              "\" at index " + speaker.index, Color.WHITE);
                } else {
                    return new TerminalOutput("ERROR: Failed to change audio device. Unable to " + 
                                              "find a speaker at index " + index, Color.RED);
                }
            } catch(NumberFormatException exception) {
                return new TerminalOutput(errorInvalidArg(args.get(0), "<int>"), Color.RED);
            }
        } else {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }

}