package org.xjge.core;

import org.xjge.graphics.Color;
import java.util.List;

/**
 * Created: Jun 24, 2021
 * <p>
 * Changes the current master volume used to attenuate the games background 
 * music.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetMusicMasterVolume extends TerminalCommand {

    /**
     * Creates a new instance of the setMusicMasterVolume command.
     */
    TCSetMusicMasterVolume() {
        super("Changes the current master volume used to attenuate the games " + 
              "background music.", 

              "Requires a floating point value between 0 and 1.",

              "setMusicMasterVolume (<float>)");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        output = null;
        
        if(args.isEmpty()) {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                return new TerminalOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    float value = Float.parseFloat(args.get(0));

                    if(value >= 0 && value <= 1) {
                        Audio.setMusicMasterVolume(value);
                        return new TerminalOutput("Music master volume changed: (" + value + ")", Color.WHITE);
                    } else {
                        return new TerminalOutput("ERROR: Value out of bounds, must be a " + 
                                  "floating point value between 0 and 1.", 
                                  Color.RED);
                    }
                } catch(NumberFormatException e) {
                    return new TerminalOutput(errorInvalidArg(args.get(0), "(float)"), Color.RED);
                }
            }
        }
    }

}