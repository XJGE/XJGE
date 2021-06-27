package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Jun 24, 2021
 */

final class TCSetSoundMasterVolume extends TerminalCommand {

    public TCSetSoundMasterVolume() {
        super("Sets the master volume the games sound effects.", 

              "Requires a float between 0 and 1.",

              "setSoundMasterVolume (<float>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.YELLOW);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.YELLOW);
            } else {
                try {
                    float value = Float.parseFloat(args.get(0));

                    if(value >= 0 && value <= 1) {
                        Audio.setSoundMasterVolume(value);
                    } else {
                        setOutput("ERROR: Value out of bounds, must be between 1 and 0.", Color.YELLOW);
                    }
                } catch(NumberFormatException e) {
                    setOutput(errorInvalidArg(args.get(0), "(float)"), Color.YELLOW);
                }
            }
        }
    }

}