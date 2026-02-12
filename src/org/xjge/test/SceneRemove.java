package org.xjge.test;

import java.util.Map;
import org.xjge.core.AssetManager;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.graphics.Shader;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneRemove extends Scene {

    public SceneRemove() {
        super("remove");
        
        AssetManager.release("test_tex1.png");
        AssetManager.release("test_tex2.png");
        AssetManager.release("test_tex3.png");
        AssetManager.release("test_tex4.png");
        AssetManager.release("test_tex5.png");
        AssetManager.release("test_tex6.png");
        AssetManager.release("test_tex7.png");
        AssetManager.release("test_tex8.png");
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