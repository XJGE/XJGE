package org.xjge.core;

import java.util.List;

//Apr 1, 2022

/**
 * @author J Hoffman
 * @since  2.1.2
 */
public class TCListConstructors extends TerminalCommand {

    public TCListConstructors(String description, String usage, String syntax) {
        super(description, usage, syntax);
        
        //Used to list the constructors of a Scene subclass.
    }

    @Override
    public void execute(List<String> args) {
    }

}
