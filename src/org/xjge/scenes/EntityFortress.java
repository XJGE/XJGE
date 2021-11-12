package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_FlipUVs;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.LightSource;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Model;

/**
 * Nov 11, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityFortress extends Entity {

    private Model fortress;
    private ArenaCaps arenaCaps;
    
    EntityFortress(float x, float y, float z) {
        super(new Vector3f(x, y, z));
        
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
        fortress.delocalizeNormal();
        fortress.translation(position);
        fortress.rotateX(-90f);
        fortress.rotateZ(90f);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        fortress.render(glProgram, lights, numLights, arenaCaps);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
        fortress.renderShadow(depthProgram);
    }

    @Override
    protected void destroy() {
    }

}
