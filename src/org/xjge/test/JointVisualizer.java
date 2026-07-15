package org.xjge.test;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.JointAttachment;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class JointVisualizer extends EntityComponent {

    private final static Shader shader;
    private final Graphics graphics = new Graphics();
    
    static {
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("shader_joint_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("shader_joint_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new Shader(shaderSourceFiles, "joint");
    }
    
    JointVisualizer() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(3);
            graphics.vertices.put(0).put(0).put(0);
            graphics.vertices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
    }
    
    void render(Camera camera, String boneName, JointAttachment jointRig) {
        graphics.modelMatrix.set(jointRig.getWorldTransform(boneName));
        
        shader.use();
        
        glPointSize(12f);
        glBindVertexArray(graphics.vao);
        
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uColor", Color.CYAN.asVector3f());
        
        glDrawArrays(GL_POINTS, 0, 1);
        glPointSize(1f);
        
        ErrorUtils.checkGLError();
    }
    
}