package org.xjge.test;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.core.LightingSystem;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class Prism extends EntityComponent {

    Color color = new Color(1, 1, 1);
    Vector3f position;
    
    private final Graphics graphics;
    private final Matrix3f normal = new Matrix3f();
    
    Prism(float x, float y, float z, float width, float height, float length) {
        position = new Vector3f(x, y, z);
        graphics = new Graphics();
        
        float halfWidth  = width / 2f;
        float halfHeight = height / 2f;
        float halfLength = length / 2f;
        
        //(vec3 position), (vec3 normal)
        float[] vertexData = new float[] {
            //Front
            -halfWidth,  halfHeight, -halfLength,  0,  0, -1,
             halfWidth,  halfHeight, -halfLength,  0,  0, -1,
             halfWidth, -halfHeight, -halfLength,  0,  0, -1,
            -halfWidth, -halfHeight, -halfLength,  0,  0, -1,
            //Back
             halfWidth,  halfHeight,  halfLength,  0,  0,  1,
            -halfWidth,  halfHeight,  halfLength,  0,  0,  1,
            -halfWidth, -halfHeight,  halfLength,  0,  0,  1,
             halfWidth, -halfHeight,  halfLength,  0,  0,  1,
            //Top
            -halfWidth,  halfHeight,  halfLength,  0,  1,  0,
             halfWidth,  halfHeight,  halfLength,  0,  1,  0,
             halfWidth,  halfHeight, -halfLength,  0,  1,  0,
            -halfWidth,  halfHeight, -halfLength,  0,  1,  0,
            //Bottom
            -halfWidth, -halfHeight, -halfLength,  0, -1,  0,
             halfWidth, -halfHeight, -halfLength,  0, -1,  0,
             halfWidth, -halfHeight,  halfLength,  0, -1,  0,
            -halfWidth, -halfHeight,  halfLength,  0, -1,  0,
            //Left
            -halfWidth,  halfHeight,  halfLength, -1,  0,  0,
            -halfWidth,  halfHeight, -halfLength, -1,  0,  0,
            -halfWidth, -halfHeight, -halfLength, -1,  0,  0,
            -halfWidth, -halfHeight,  halfLength, -1,  0,  0,
            //Right
             halfWidth,  halfHeight, -halfLength,  1,  0,  0,
             halfWidth,  halfHeight,  halfLength,  1,  0,  0,
             halfWidth, -halfHeight,  halfLength,  1,  0,  0,
             halfWidth, -halfHeight, -halfLength,  1,  0,  0,
        };
        
        int[] indexData = new int[] {
             0,  1,  2,  2,  3,  0, //Front
             4,  5,  6,  6,  7,  4, //Back
             8,  9, 10, 10, 11,  8, //Top
            12, 13, 14, 14, 15, 12, //Bottom
            16, 17, 18, 18, 19, 16, //Left
            20, 21, 22, 22, 23, 20, //Right
        };
        
        graphics.vertices = MemoryUtil.memAllocFloat(vertexData.length);
        graphics.indices  = MemoryUtil.memAllocInt(indexData.length);
        
        graphics.vertices.put(vertexData);
        graphics.indices.put(indexData);
        graphics.vertices.flip();
        graphics.indices.flip();
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        MemoryUtil.memFree(graphics.vertices);
        MemoryUtil.memFree(graphics.indices);
    }
    
    void render(Camera camera) {
        graphics.modelMatrix.translation(position);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBindVertexArray(graphics.vao);
        
        ObjectShader.getInstance().use();
        ObjectShader.getInstance().setUniform("uNumLights", LightingSystem.getActiveCount());
        ObjectShader.getInstance().setUniform("uColor", color.asVector3f());
        ObjectShader.getInstance().setUniform("uNormal", true, normal);
        ObjectShader.getInstance().setUniform("uModel", false, graphics.modelMatrix);
        ObjectShader.getInstance().setUniform("uView", false, camera.getViewMatrix());
        ObjectShader.getInstance().setUniform("uProjection", false, camera.getProjectionMatrix());
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}