package org.xjge.core;

import java.util.LinkedList;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.GLShader;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Skybox implements AssetReloadListener {
    
    private final int cubemapHandle;
    
    private float bloomThreshold;
    
    private final Graphics graphics;
    private final GLProgram shader;
    
    private final Matrix4f newView = new Matrix4f();
    
    private static final int[] TARGETS = {
        GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };
    
    private final Texture[] faces;
    
    public Skybox(Texture right, Texture left, Texture top, Texture bottom, Texture front, Texture back, boolean useLinearFilter) {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_skybox_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_skybox_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new GLProgram(shaderSourceFiles, "xjge_skybox");
        
        cubemapHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapHandle);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, (useLinearFilter) ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        
        faces = new Texture[] {right, left, top, bottom, front, back};
        bindFaces();
        
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
    
    private void bindFaces() {
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapHandle);
        
        for(int i = 0; i < faces.length; i++) {
            var face = faces[i];
            
            face.addAssetListener(this);
            face.bind(GL_TEXTURE_2D);
            
            glTexImage2D(TARGETS[i], 0, GL_RGBA, face.getWidth(), face.getHeight(), 0, 
                         GL_RGBA, GL_UNSIGNED_BYTE, face.getImageData());
        }
        
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }
    
    /**
     * Renders the skybox using the images provided through its constructor. 
     * The view matrix of the camera currently rendering the scene is included 
     * here to create the illusion of distance.
     * 
     * @param viewMatrix the view matrix of the viewport camera currently 
     *                   rendering the scene
     * @param projMatrix the projection matrix of the viewport camera currently 
     *                   rendering the scene
     */
    void render(Matrix4f viewMatrix, Matrix4f projMatrix) {
        glDepthMask(false);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapHandle);
        glBindVertexArray(graphics.vao);
        
        newView.set(viewMatrix);
        newView.m30(0);
        newView.m31(0);
        newView.m32(0);
        
        shader.use();
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uView", false, newView);
        shader.setUniform("uProjection", false, projMatrix);
        shader.setUniform("uSkyTexture", 0);
        shader.setUniform("uBloomThreshold", XJGE.enableBloom ? bloomThreshold : 0f);
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDepthMask(true);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Provides the model matrix of the skybox. This can be used to achieve 
     * various effects such as rotation.
     * 
     * @return the model matrix of the skybox 
     */
    public Matrix4f getModelMatrix() {
        return graphics.modelMatrix;
    }
    
    public void delete() {
        glDeleteTextures(cubemapHandle);
        graphics.freeBuffers();
        //TODO: unload shader?
    }
    
    /**
     * Specifies the value which will be used to indicate how bright the surface 
     * of objects must be before the bloom effect is applied to it. The lower 
     * the brightness threshold, the more abundant bloom will be.
     * 
     * @param bloomThreshold a number between 0 and 1 that the brightness of a surface will need to exceed
     */
    public void setBloomThreshold(float bloomThreshold) {
        this.bloomThreshold = XJGE.clampValue(0f, 1f, bloomThreshold);
    }

    @Override
    public void onAssetReload(Asset asset) {
        if(asset instanceof Texture) bindFaces();
    }

}