package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Entity;
import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.LightSource;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Atlas;
import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.graphics.SpriteAnimation;
import dev.theskidster.xjge2.graphics.Texture;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

public class TestEntity2 extends Entity {

    Graphics g;
    Texture texture;
    Atlas atlas;
    SpriteAnimation animation;
    
    public TestEntity2(float x, float y, float z) {
        super(new Vector3f(x, y, z));
        
        g       = new Graphics();
        texture = new Texture("spr_engineicons.png");
        atlas   = new Atlas(texture, 64, 64);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 tex coords)
            g.vertices   .put(0).put(0.5f).put(0)   .put(0)                  .put(0);
            g.vertices.put(0.5f).put(0.5f).put(0)   .put(atlas.subImageWidth).put(0);
            g.vertices.put(0.5f)   .put(0).put(0)   .put(atlas.subImageWidth).put(atlas.subImageHeight);
            g.vertices   .put(0)   .put(0).put(0)   .put(0)                  .put(atlas.subImageHeight);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        
        List<Vector2i> frames = new ArrayList<>() {{
            add(new Vector2i());
            add(new Vector2i(1, 0));
            add(new Vector2i(2, 0));
        }};
        
        animation = new SpriteAnimation(15, frames);
        
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        g.modelMatrix.translation(position);
        animation.updateAnimation(atlas);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 7);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", atlas.texCoords);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    protected void destroy() {
    }

}