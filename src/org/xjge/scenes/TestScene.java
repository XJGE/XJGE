package org.xjge.scenes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Control;
import org.xjge.core.Input;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Puppet;
import org.xjge.core.Scene;
import org.xjge.core.StopWatch;
import org.xjge.core.Timer;
import org.xjge.graphics.GLProgram;

/**
 * Feb 5, 2022
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene implements PropertyChangeListener {

    int prevTime;
    
    boolean start;
    
    private StopWatch stopWatch = new StopWatch();
    private Timer timer;
    private Puppet puppet = new Puppet(this);
    
    public TestScene() {
        super("asd");
        
        timer = new Timer(5, 60, this);
        
        puppet.commands.put(Control.CROSS, new CommandStart(timer));
        
        Input.setDevicePuppet(KEY_MOUSE_COMBO, puppet);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        
        /*
        if(Game.tick(60) && count < 3) {
            count++;
        }
        
        if(!start && count == 3) {
            System.out.println(Game.getTickCount());
            
            start = true;
        }*/
        
        /*
        if(stopWatch.tick(5, 60, false)) {
            System.out.println(stopWatch.getTime() + " " + Game.getTickCount());
        }
        */
        
        if(timer.getTime() != prevTime) {
            System.out.println(timer.getTime());
            prevTime = timer.getTime();
        }
        
        timer.update();
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "finished" -> {
                if((Boolean) evt.getNewValue() == true) System.out.println("timer finished.");
            }
        }
    }

}
