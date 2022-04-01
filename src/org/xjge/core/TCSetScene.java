package org.xjge.core;

import org.xjge.graphics.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
            try {
                Class<?> c = Class.forName(XJGE.getScenesFilepath() + args.get(0));

                if(!c.getSimpleName().equals("Scene") && Scene.class.isAssignableFrom(c)) {
                    Constructor[] cons   = c.getConstructors();
                    Constructor conToUse = null;
                    
                    for(int i = 0; i < cons.length && conToUse == null; i++) {
                        if(cons[i].getParameterCount() == args.size() - 1) {
                            conToUse = cons[i];
                        }
                    }
                    
                    if(conToUse != null) {
                            if(args.size() == 1) {
                            Game.setScene((Scene) conToUse.newInstance());
                        } else {
                            var params = new ArrayList<String>();
                            for(int i = 1; i < args.size(); i++) params.add(args.get(i));
                            Game.setScene((Scene) conToUse.newInstance((Object[]) params.toArray()));
                        }
                    } else {
                        //TODO: throw exception.
                        System.out.println("con is null");
                    }

                    setOutput("Current scene changed to \"" + Game.getSceneName() + "\"", Color.WHITE);
                } else {
                    setOutput("ERROR: Invalid argument. Must be a subclass of Scene.", Color.RED);
                }
            } catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | 
                    InstantiationException | InvocationTargetException | NoClassDefFoundError ex) {
                setOutput("ERROR: \"" + ex.getClass().getSimpleName() + "\" caught while " + 
                          "attempting to change the current scene.", 
                          Color.RED);
                
                System.out.println(ex);
            }
        }
    }

}