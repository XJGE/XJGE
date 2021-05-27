package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.TerminalCommand;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.ui.Font;
import java.util.List;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public class Test {

    /*
    TODO:
    this class and package are temporary and provided for testing purposes only!
    they should not be included in releases!
    */
    
    public static void main(String args[]) {
        
        Vector2i res = new Vector2i(384, 216);
        
        XJGE.init("/dev/theskidster/xjge2/assets/", true, null, true);
        
        //Font.DEFAULT_SIZE = 12;
        
        XJGE.addTerminalCommand("bleh", new C());
        
        Game.setScene(new TestScene());
        
        XJGE.start();
        
    }
    
    private static class C extends TerminalCommand {

        public C() {
            super("", "", "");
        }

        @Override
        public void execute(List<String> args) {
        }
        
    }
    
}