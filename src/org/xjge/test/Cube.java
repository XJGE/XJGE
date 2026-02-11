package org.xjge.test;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;
import org.xjge.graphics.Graphics;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class Cube extends EntityComponent {

    Color color = new Color(1, 1, 1);
    
    private final Graphics graphics;
    private final GLProgram shader;
    
    Cube() {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("shader_cube_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("shader_cube_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "cube");
        
        graphics = new Graphics();
        
        float[] vertexData = new float[] {
            -1,  1, -1, //0
             1,  1, -1, //1
             1, -1, -1, //2
            -1, -1, -1, //3
            -1,  1,  1, //4
             1,  1,  1, //5
             1, -1,  1, //6
            -1, -1,  1  //7
        };
        
        int[] indexData = new int[] {
            1, 5, 6, 6, 2, 1, //Right (+X)
            4, 0, 3, 3, 7, 4, //Left (-X)
            4, 5, 1, 1, 0, 4, //Top (+Y)
            3, 2, 6, 6, 7, 3, //Bottom (-Y)
            5, 4, 7, 7, 6, 5, //Front (+Z)
            0, 1, 2, 2, 3, 0  //Back (-Z)
        };
        
        graphics.vertices = MemoryUtil.memAllocFloat(vertexData.length);
        graphics.indices  = MemoryUtil.memAllocInt(indexData.length);
        
        graphics.vertices.put(vertexData);
        graphics.indices.put(indexData);
        graphics.vertices.flip();
        graphics.indices.flip();
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        MemoryUtil.memFree(graphics.vertices);
        MemoryUtil.memFree(graphics.indices);
    }
    
    void render(Camera camera) {
        glBindVertexArray(graphics.vao);
        
        shader.use();
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uColor", color.asVector3f());
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        ErrorUtils.checkGLError();
    }
    
}