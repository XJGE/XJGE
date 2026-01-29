package org.xjge.core;

import java.util.LinkedList;
import org.joml.Matrix3f;
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
public final class Skybox2 implements AssetReloadListener {
    
    private final int cubemapHandle;
    
    private final Graphics graphics;
    private static final GLProgram skyboxShader;
    
    private final Matrix3f tempView = new Matrix3f();
    private final Matrix4f newView  = new Matrix4f();
    private final Matrix4f projMatrix2  = new Matrix4f();
    
    private static final int[] TARGETS = {
        GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
        GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };
    
    private final Texture[] faces;
    
    static {
        var shaderSourceFiles = new LinkedList<GLShader>() {{
            add(GLShader.load("xjge_shader_skybox_vertex.glsl", GL_VERTEX_SHADER));
            add(GLShader.load("xjge_shader_skybox_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        skyboxShader = new GLProgram(shaderSourceFiles, "default");
        
        XJGE.glPrograms.put("skybox", skyboxShader);
    }
    
    public Skybox2(Texture right, Texture left, Texture top, Texture bottom, Texture front, Texture back, boolean useLinearFilter) {
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
        
        graphics.vertices = MemoryUtil.memAllocFloat(192);
        graphics.indices  = MemoryUtil.memAllocInt(36);
        
        //Front
        graphics.vertices.put(-1) .put(1).put(-1); //0
        graphics.vertices .put(1) .put(1).put(-1); //1
        graphics.vertices .put(1).put(-1).put(-1); //2
        graphics.vertices.put(-1).put(-1).put(-1); //3
        
        //Back
        graphics.vertices .put(1) .put(1).put(1);  //4
        graphics.vertices.put(-1) .put(1).put(1);  //5
        graphics.vertices.put(-1).put(-1).put(1);  //6
        graphics.vertices .put(1).put(-1).put(1);  //7
        
        //Top
        graphics.vertices.put(-1).put(1) .put(1);  //8
        graphics.vertices .put(1).put(1) .put(1);  //9
        graphics.vertices .put(1).put(1).put(-1);  //10
        graphics.vertices.put(-1).put(1).put(-1);  //11
        
        //Bottom
        graphics.vertices.put(-1).put(-1).put(-1); //12
        graphics.vertices .put(1).put(-1).put(-1); //13
        graphics.vertices .put(1).put(-1) .put(1); //14
        graphics.vertices.put(-1).put(-1) .put(1); //15
        
        //Left
        graphics.vertices.put(-1) .put(1) .put(1); //16
        graphics.vertices.put(-1) .put(1).put(-1); //17
        graphics.vertices.put(-1).put(-1).put(-1); //18
        graphics.vertices.put(-1).put(-1) .put(1); //19
        
        //Right
        graphics.vertices.put(1) .put(1).put(-1);  //20
        graphics.vertices.put(1) .put(1) .put(1);  //21
        graphics.vertices.put(1).put(-1) .put(1);  //22
        graphics.vertices.put(1).put(-1).put(-1);  //23
        
        graphics.indices.put(0).put(1).put(2).put(2).put(3).put(0);       //Front
        graphics.indices.put(4).put(5).put(6).put(6).put(7).put(4);       //Back
        graphics.indices.put(8).put(9).put(10).put(10).put(11).put(8);    //Top
        graphics.indices.put(12).put(13).put(14).put(14).put(15).put(12); //Bottom
        graphics.indices.put(16).put(17).put(18).put(18).put(19).put(16); //Left
        graphics.indices.put(20).put(21).put(22).put(22).put(23).put(20); //Right
        
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
    
    void render(Matrix4f viewMatrix) {
        skyboxShader.use();
        
        glDisable(GL_CULL_FACE);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(false);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapHandle);
        glBindVertexArray(graphics.vao);
        
        viewMatrix.get3x3(tempView);
        newView.set(tempView);
        
        skyboxShader.setUniform("uModel", false, graphics.modelMatrix);
        skyboxShader.setUniform("uView", false, newView);
        skyboxShader.setUniform("uSkyTexture", 2);
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDepthMask(true);
        glDepthFunc(GL_LESS);
        
        skyboxShader.setUniform("uView", false, viewMatrix);
        
        ErrorUtils.checkGLError();
    }
    
    public Matrix4f getModelMatrix() {
        return graphics.modelMatrix;
    }

    @Override
    public void onAssetReload(Asset asset) {
        if(asset instanceof Texture) bindFaces();
    }

}