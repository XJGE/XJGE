package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentMudBall extends Component {
    
    private boolean active;
    private boolean landed;
    
    private float flightTime;
    private float elapsedTime;
    
    private Vector3f startPos;
    private Vector3f targetPos;
    private Vector3f currentPos;
    
    private final Graphics graphics = new Graphics();
    
    private static final Texture texture;
    
    static {
        texture = new Texture("image_mudball.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    ComponentMudBall() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(32);
            graphics.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texcoords)
            graphics.vertices.put(-0.5f).put( 0.5f).put(0) .put(0).put(1);
            graphics.vertices.put( 0.5f).put( 0.5f).put(0) .put(1).put(1);
            graphics.vertices.put( 0.5f).put(-0.5f).put(0) .put(1).put(0);
            graphics.vertices.put(-0.5f).put(-0.5f).put(0) .put(0).put(0);
            
            graphics.indices.put(0).put(1).put(2);
            graphics.indices.put(2).put(3).put(0);
            
            graphics.vertices.flip();
            graphics.indices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    void launch(Vector3f start, Vector3f target, float accuracy) {
        startPos = new Vector3f(start);
        
        //Scale the target distance based on accuracy value provided during RTR
        Vector3f dir = new Vector3f(target).sub(start).mul(accuracy);
        targetPos = new Vector3f(start).add(dir);

        currentPos = start;
        flightTime = 1.0f;
        elapsedTime = 0f;
        active = true;
    }
    
    void update(double delta, Map<Vector3i, GridSpace> gridSpaces, Entity owner) {
        if(!active) return;

        elapsedTime += delta;
        float t = Math.min(elapsedTime / flightTime, 1f);

        //Interpolate horizontally (x/z)
        currentPos.x = XJGE.lerp(startPos.x, targetPos.x, t);
        currentPos.z = XJGE.lerp(startPos.z, targetPos.z, t);

        //Add arc vertically (y)
        float height = 2.0f * (1 - (t * 2 - 1) * (t * 2 - 1)); //Parabola 0->1->0
        currentPos.y = XJGE.lerp(startPos.y, targetPos.y, t) + height;

        if(t >= 1f) {
            active = false;
            landed = true;
            
            int centerX = Math.round(currentPos.x);
            int centerZ = Math.round(currentPos.z);
            
            for(int z = -1; z <= 1; z++) {
                for(int x = -1; x <= 1; x++) {
                    GridSpace space = gridSpaces.get(new Vector3i(centerX + x, 0, centerZ + z));
                    
                    if(space != null && space.type != GridSpace.TYPE_SOLID) {
                        space.muddy = true;
                    }
                }
            }
            
            owner.removeFromScene();
        }
    }
    
    void render(Map<String, GLProgram> glPrograms, Vector3f position, Vector3f cameraPos) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        
        glActiveTexture(GL_TEXTURE0);
        texture.bind(GL_TEXTURE_2D);
        
        glBindVertexArray(graphics.vao);
        
        GLProgram glProgram = glPrograms.get("mudball");
        glProgram.use();
        
        graphics.modelMatrix.translation(position);
        graphics.modelMatrix.billboardSpherical(position, cameraPos);
        graphics.modelMatrix.scale(0.75f);
        
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uColor", Color.BROWN.asVector3f());
        glProgram.setUniform("uModel", false, graphics.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        glDisable(GL_ALPHA_TEST);
        
        ErrorUtils.checkGLError();
    }
    
    void destroy() {
        /*
        TODO: might be worthwhile to add a destroy() method to Scene3D that allows 
        components to free buffers/resources after their owner has requested removal
        */
        graphics.freeBuffers();
    }
    
    
    boolean landed() {
        return landed;
    }
    
}