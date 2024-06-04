package org.xjge.test;

//Created: May 30, 2024

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Game;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Puppet;
import org.xjge.core.Scene;
import org.xjge.graphics.GLProgram;


/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    int count;
    
    final Puppet puppet;
    
    public TestScene() {
        super("test");
        
        var controls = new HashMap<Control, Command>() {{
            put(Control.DPAD_UP,    new CommandTest());
            put(Control.DPAD_DOWN,  new CommandTest());
            put(Control.DPAD_LEFT,  new CommandTest());
            put(Control.DPAD_RIGHT, new CommandTest());
        }};
        
        puppet = new Puppet("test", controls);
        
        puppet.setInputDevice(KEY_MOUSE_COMBO);
        puppet.setInputDevice(KEY_MOUSE_COMBO);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        /*
        if(Game.tick(60)) {
            count++;
        }
        
        if(count == 5) {
            puppet.setInputDevice(GLFW_JOYSTICK_1);
            System.out.println("changed input device puppet to joystick 1");
        }
        */
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }
    
    public class CommandTest extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(buttonPressedOnce()) System.out.println(getButtonID());
        }
        
    }

}