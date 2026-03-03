package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;
import org.xjge.modeling.Model2;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneModel extends Scene {

    public SceneModel() {
        super("test_model");
        
        XJGE.setClearColor(new Color(0.467f, 0.533f, 1f));
        
        Model2 model = Model2.load("yshtola.fbx");
        
        
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

}