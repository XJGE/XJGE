package org.xjge.core;

import org.xjge.graphics.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;

//Created: Jun 27, 2021

/**
 * Changes the current scene to render.
 * <p>
 * NOTE: The package containing the scenes returned by 
 * {@link org.xjge.core.XJGE#getScenesFilepath() XJGE.getScenesFilepath()} must 
 * be named "scenes" otherwise this command will fail.
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
                        
                        
                        
                        Constructor[] cons = c.getConstructors();
                        
                        System.out.println(cons[0].getParameterCount());
                        
                        Constructor con = c.getConstructor(Scene.class.getClasses());
                        
                        System.out.println("asdf");
                        
                        Parameter[] params = con.getParameters();
                        
                        if(params.length == 0) {
                            Game.setScene((Scene) con.newInstance(new Object[0]));
                            setOutput("Current scene changed to \"" + Game.getSceneName() + "\"", Color.WHITE);
                        } else {
                            Game.setScene((Scene) con.newInstance(new Object[0]));
                        }
                    } else {
                        setOutput("ERROR: Invalid argument. Must be a subclass of Scene.", Color.RED);
                    }
                } catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                        InstantiationException | NoSuchMethodException | SecurityException | 
                        InvocationTargetException | NoClassDefFoundError ex) {
                    setOutput("ERROR: \"" + ex.getClass().getSimpleName() + "\" caught while " + 
                              "attempting to change the current scene.", 
                              Color.RED);
                }
            }
        }
    }

}