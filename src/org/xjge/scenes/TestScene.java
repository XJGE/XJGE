package org.xjge.scenes;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Game;
import org.xjge.core.Light;
import org.xjge.core.Scene;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Model;

/**
 * Dec 15, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    private Model model;
    
    public TestScene() {
        super("test");
        
        model = new Model("mod_musket.fbx");
        
        model.listAnimations();
        model.setCurrentAnimation("Idle", 0);
        
        lights[0] = Light.daylight();
        
        //Game.enableBloom = true;
        //Game.setBloomThreshold(10f);
        
        //Game.setClearColor(Color.GRAY);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        model.updateAnimation(trueDelta);
        model.delocalizeNormal();
        model.translation(0, 0, 0);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        glPrograms.get("default").use();
        model.render(glPrograms.get("default"), lights, 256, depthTexHandle);
    }

    @Override
    public void exit() {
    }

}
