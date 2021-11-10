package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Light;
import org.xjge.graphics.Model;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    private float angle = 0;
    
    private Skybox skybox = new Skybox("sky_noon_top.png", "sky_noon_center.png", "sky_noon_bottom.png", true);
    
    private Model fortress;
    private ArenaCaps arenaCaps;
    
    private EntityPlane plane1;
    private EntityPlane plane2;
    private EntityPlane plane3;
    private EntityPlane plane4;
    private EntityCube cube;
    
    public TestScene() {
        super("test");
        
        setSkybox(skybox);
        
        /*
        TODO:
         - shadow mapping should be as simple as setting up a skybox and setting 
           entities model matrix via renderShadow()
         - models should still exhibit lighting even with shadows disabled
         - clean up any loose ends- add documentation.
        */
        
        plane1 = new EntityPlane(0, -3, 0, 50, 50, Color.BLUE);
        plane2 = new EntityPlane(0, 3, 0, 5, 5, Color.RED);
        plane3 = new EntityPlane(-1, 4, 0, 2, 2, Color.GREEN);
        plane4 = new EntityPlane(30, 3, 30, 2, 2, Color.YELLOW);
        cube   = new EntityCube(3, 7, 4, 2, 2, 2);
        
        entities.put("plane1", plane1);
        entities.put("plane2", plane2);
        entities.put("plane3", plane3);
        entities.put("plane4", plane4);
        entities.put("cube", cube);
        
        //TODO: try teapot model.
        
        /*
        fortress = new Model("mod_arena.fbx", 
             aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_FixInfacingNormals);
        
        fortress.bindMeshTexture("Walls");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        fortress.bindMeshTexture("Spawns");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        arenaCaps = new ArenaCaps();
        */
        
        addLightAtIndex(0, new Light(1, 0.5f, new Vector3f(16, 26, 14), Color.WHITE, Color.WHITE));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        skybox.getModelMatrix().rotationY((float) Math.toRadians(angle -= 0.01f));
        
        /*
        fortress.delocalizeNormal();
        fortress.translation(0, 0, 0);
        fortress.rotateX(-90f);
        fortress.rotateZ(90f);
        */
        
        entities.values().forEach(entity -> entity.update(targetDelta, trueDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        //fortress.render(glPrograms.get("default"), getLightSources(), getNumLights(), arenaCaps);
        
        glPrograms.get("default").use();
        entities.values().forEach(entity -> entity.render(glPrograms.get("default"), camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}