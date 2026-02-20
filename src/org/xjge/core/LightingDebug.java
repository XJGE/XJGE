package org.xjge.core;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;
import static org.xjge.core.LightingSystem.MAX_LIGHTS;
import org.xjge.graphics.Atlas;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;
import org.xjge.graphics.Texture;

/**
 * Exposes the positions of light sources during debug mode
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class LightingDebug {

    private static final int INSTANCE_FLOATS = 8;
    private final int vboInstance = glGenBuffers();

    private final Texture engineIcons;
    private final Shader shader;
    private final Graphics graphics;
    private final Atlas atlas;
    private final Vector2i atlasKey = new Vector2i();

    private final FloatBuffer instanceBuffer = BufferUtils.createFloatBuffer(MAX_LIGHTS * INSTANCE_FLOATS);

    LightingDebug(Texture engineIcons) {
        this.engineIcons = engineIcons;

        var shaderStages = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("xjge_shader_light_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("xjge_shader_light_fragment.glsl", GL_FRAGMENT_SHADER));
        }};

        shader   = new Shader(shaderStages, "xjge_lighting_debug");
        graphics = new Graphics();
        atlas    = new Atlas(engineIcons, 40, 40);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(4 * 4);
            graphics.indices  = stack.mallocInt(6);

            //(vec2 position), (vec2 texCoords)
            float[] vertexData = {
                -0.5f, -0.5f,   0f, 0f,
                 0.5f, -0.5f,   atlas.subImageWidth, 0f,
                 0.5f,  0.5f,   atlas.subImageWidth, atlas.subImageHeight,
                -0.5f,  0.5f,   0f, atlas.subImageHeight
            };

            graphics.vertices.put(vertexData).flip();
            graphics.indices.put(0).put(1).put(2).put(2).put(3).put(0).flip();
        }

        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        glBindVertexArray(graphics.vao);

        glBindBuffer(GL_ARRAY_BUFFER, vboInstance);
        glBufferData(GL_ARRAY_BUFFER, MAX_LIGHTS * INSTANCE_FLOATS * (long) Float.BYTES, GL_DYNAMIC_DRAW);

        int stride = INSTANCE_FLOATS * Float.BYTES;

        //(vec3 position)
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);

        //(vec2 texCoord offset)
        glVertexAttribPointer(4, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);

        //(vec3 color)
        glVertexAttribPointer(5, 3, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(5);
        glVertexAttribDivisor(5, 1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    private int uploadInstanceData(int activeCount, Light2[] lights) {
        instanceBuffer.clear();

        int count = 0;

        for(Light2 light : lights) {
            if(!light.enabled)continue;
            
            Vector2f texCoords = switch (light.type) {
                default    -> atlas.subImageOffsets.get(atlasKey.set(0, 1));
                case SPOT  -> atlas.subImageOffsets.get(atlasKey.set(0, 2));
                case WORLD -> atlas.subImageOffsets.get(atlasKey.set(0, 3));
            };
            
            instanceBuffer.put(light.position.x).put(light.position.y).put(light.position.z);
            instanceBuffer.put(texCoords.x).put(texCoords.y);
            instanceBuffer.put(light.color.getRed()).put(light.color.getGreen()).put(light.color.getBlue());

            count++;

            if(count >= activeCount) break;
        }

        instanceBuffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboInstance);
        glBufferSubData(GL_ARRAY_BUFFER, 0, instanceBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return count;
    }
    
    void draw(int activeCount, Light2[] lights, Camera camera) {
        if(activeCount == 0) return;
        
        int instanceCount = uploadInstanceData(activeCount, lights);
        
        glDisable(GL_DEPTH_TEST);
        glBindVertexArray(graphics.vao);
        glActiveTexture(GL_TEXTURE0);
        engineIcons.bind(GL_TEXTURE_2D);
        
        shader.use();
        shader.setUniform("uTexture", 0);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uIconScale", 1f);
        
        glDrawElementsInstanced(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0, instanceCount);
        glBindVertexArray(0);
        
        ErrorUtils.checkGLError();
    }
    
}