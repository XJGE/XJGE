package org.xjge.core;

import org.xjge.graphics.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

//Created: Jun 27, 2021

/**
 * Changes the current scene to render.
 * <p>
 * NOTE: Scene subclasses must provide a default constructor with no parameters
 * otherwise using this command will generate an exception.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class TCSetScene extends TerminalCommand {

    /**
     * Creates a new instance of the setScene command.
     */
    TCSetScene() {
        super("Changes the current scene to render.", 

              "Pass the class name of the scene you want to enter. Class names " +
              "are case sensitive and should not include parenthesis or file " +
              "extensions.",

              "setScene (<string>)");
    }

    @Override
    public void execute(List<String> args) {
        output = null;

        if(args.isEmpty()) {
            setOutput(errorNotEnoughArgs(1), Color.RED);
        } else {
            if(args.size() > 1) {
                setOutput(errorTooManyArgs(args.size(), 1), Color.RED);
            } else {
                try {
                    Class<?> c = Class.forName(XJGE.getScenesFilepath() + args.get(0));

                    if(!c.getSimpleName().equals("Scene") && Scene.class.isAssignableFrom(c)) {
                        //Assumes the scenes constructor has no parameters.
                        //TODO: allow constructors with parameters?
                        Game.setScene((Scene) c.getConstructor().newInstance());
                        setOutput("Current scene changed to \"" + Game.getSceneName() + "\"", Color.WHITE);
                    } else {
                        setOutput("ERROR: Invalid argument. Must be a subclass of Scene.", Color.RED);
                    }
                } catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                        InstantiationException | InvocationTargetException | NoSuchMethodException | 
                        NoClassDefFoundError ex) {
                    setOutput("ERROR: \"" + ex.getClass().getSimpleName() + "\" caught while " + 
                              "attempting to change the current scene.", 
                              Color.RED);
                }
            }
        }
    }

}