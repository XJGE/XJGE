package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

//Created: Jun 27, 2021

/**
 * Changes the current scene to render.
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

              "Pass the class name of the desired level, should not include " + 
              " parentheses or extension.",

              "setLevel (<string>)");
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
                        Constructor con = c.getConstructor(Scene.class.getClasses());
                        Game.setScene((Scene) con.newInstance(new Object[0]));
                    } else {
                        setOutput("ERROR: Invalid argument. Must be a subclass of Scene.", Color.RED);
                    }
                } catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                        InstantiationException | NoSuchMethodException | SecurityException | 
                        InvocationTargetException | NoClassDefFoundError ex) {
                    setOutput(errorInvalidArg(args.get(0), "<string>"), Color.RED);
                }
            }
        }
    }

}