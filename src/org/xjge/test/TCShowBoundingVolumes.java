package org.xjge.test;

import java.util.List;
import org.xjge.core.TerminalCommand;
import org.xjge.core.TerminalOutput;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class TCShowBoundingVolumes extends TerminalCommand {

    private static boolean showBoundingVolumes = true; //TODO: true by defualt for now
    
    public TCShowBoundingVolumes() {
        super("Changes whether or not bounding volumes are visible.", 
              
              useGenericShowing("bounding volume visibility"), 
              
              "showBoundingVolumes [true|false]");
    }

    @Override
    public TerminalOutput execute(List<String> args) {
        if(!args.isEmpty()) {
            String parameter = args.get(0);

            if(parameter.equals("true") || parameter.equals("false")) {
                boolean value = Boolean.parseBoolean(parameter);
                showBoundingVolumes = value;
                return new TerminalOutput("Bounding volume visibility changed: (" + value + ")", Color.WHITE);
            } else {
                return new TerminalOutput(errorInvalidArg(parameter, "(true) or (false)"), Color.RED);
            }
        } else {
            showBoundingVolumes = !showBoundingVolumes;
            return new TerminalOutput("Bounding volume visibility changed: (" + showBoundingVolumes + ")", Color.WHITE);
        }
    }
    
    boolean getValue() {
        return showBoundingVolumes;
    }

}