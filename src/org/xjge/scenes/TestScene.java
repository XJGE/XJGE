package org.xjge.scenes;

import java.util.Map;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import org.xjge.core.Camera;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.graphics.GLProgram;
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
    
    public TestScene() {
        super("test");
        
        setSkybox(skybox);
        
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
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        skybox.getModelMatrix().rotationY((float) Math.toRadians(angle -= 0.01f));
        
        fortress.delocalizeNormal();
        fortress.translation(0, 0, 0);
        fortress.rotateX(-90f);
        fortress.rotateZ(90f);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        fortress.render(glPrograms.get("default"), getLightSources(), getNumLights(), arenaCaps);
    }

    @Override
    public void exit() {
    }

}