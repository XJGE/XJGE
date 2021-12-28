package org.xjge.scenes;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Light;
import org.xjge.core.Scene;
import org.xjge.core.ShadowMap;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    private TestEntity background = new TestEntity(64, 64, -10, 128, true);
    
    private TileRenderer tileRenderer = new TileRenderer();
    private Map<Vector2i, Tile> tiles = new HashMap<>();
    
    public TestScene() {
        super("test");
        
        XJGE.setViewportCamera(GLFW_JOYSTICK_1, new Camera2D());
        
        entities.put("test2", new TestEntity(44, 48, 4, 16, false));
        entities.put("test1", new TestEntity(40, 40, 0, 16, false));
        
        lights[0] = new Light(1, 0.55f, 1, new Vector3f(-30, 30, 60), Color.WHITE);
        setShadowMap(new ShadowMap(0.00003f, 0.0009f, 200f, 100f, true, 1920, 1920, true, 0));
        
        tiles.put(new Vector2i(), new Tile(1, new Vector3f(48, 48, -3)));
        
        //TODO: implement instanced objects that can be shaded by shadowsM
        
        //Game.enableBloom = true;
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
        background.update(targetDelta, trueDelta);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        background.render(glPrograms.get("default"), camera, lights, depthTexHandle);
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, lights, depthTexHandle));
        //tileRenderer.draw(glPrograms.get("default"), tiles, depthTexHandle);
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
        entities.values().forEach(entity -> entity.renderShadow(depthProgram));
    }

    @Override
    public void exit() {
    }

}
