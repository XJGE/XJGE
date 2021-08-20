package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Input;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Puppet;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;

/**
 * Aug 19, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    class Move extends Command {

        private String dir;
        
        Move(String dir) {
           this.dir = dir; 
        }
        
        @Override
        public void execute() {
            if(axisMoved()) {
                System.out.println(dir + " " + getInputValue());
            }
        }
        
    }
    
    Puppet puppet = new Puppet(this);
    
    public TestScene() {
        super("test");
        
        puppet.commands.put(Control.LEFT_STICK_X, new Move("x"));
        puppet.commands.put(Control.LEFT_STICK_Y, new Move("y"));
        
        Input.setDevicePuppet(KEY_MOUSE_COMBO, puppet);
        
        //Window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        
    }

    @Override
    public void exit() {
        
    }

}
