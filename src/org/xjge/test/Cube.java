package org.xjge.test;

import java.util.LinkedList;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;
import org.xjge.graphics.Graphics;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class Cube extends EntityComponent {

    Color color = new Color(1, 1, 1);
    Vector3f position;
    
    private final Graphics graphics;
    private final Shader shader;
    private final Matrix3f normal = new Matrix3f();
    
    Cube(float x, float y, float z, float size) {
        position = new Vector3f(x, y, z);
        
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("shader_cube_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("shader_cube_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new Shader(shaderSourceFiles, "cube");
        
        XJGE.addGLProgram("test_cube", shader);
        
        graphics = new Graphics();
        
        //(vec3 position), (vec3 normal)
        float[] vertexData = new float[] {
            //Front
            -size,  size, -size,  0,  0, -1,
             size,  size, -size,  0,  0, -1,
             size, -size, -size,  0,  0, -1,
            -size, -size, -size,  0,  0, -1,
            //Back
             size,  size,  size,  0,  0,  1,
            -size,  size,  size,  0,  0,  1,
            -size, -size,  size,  0,  0,  1,
             size, -size,  size,  0,  0,  1,
            //Top
            -size,  size,  size,  0,  1,  0,
             size,  size,  size,  0,  1,  0,
             size,  size, -size,  0,  1,  0,
            -size,  size, -size,  0,  1,  0,
            //Bottom
            -size, -size, -size,  0, -1,  0,
             size, -size, -size,  0, -1,  0,
             size, -size,  size,  0, -1,  0,
            -size, -size,  size,  0, -1,  0,
            //Left
            -size,  size,  size, -1,  0,  0,
            -size,  size, -size, -1,  0,  0,
            -size, -size, -size, -1,  0,  0,
            -size, -size,  size, -1,  0,  0,
            //Right
             size,  size, -size,  1,  0,  0,
             size,  size,  size,  1,  0,  0,
             size, -size,  size,  1,  0,  0,
             size, -size, -size,  1,  0,  0,
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
        
        shader.use();
        shader.setUniform("uColor", color.asVector3f());
        shader.setUniform("uNormal", true, normal);
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}