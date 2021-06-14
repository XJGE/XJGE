package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Graphics;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

public final class Skybox {
    
    private final Cubemap cubemap;
    private final Graphics g;
    
    private final Matrix3f tempView = new Matrix3f();
    private final Matrix4f newView  = new Matrix4f();
    
    public Skybox(String topFilename, String centerFilename, String bottomFilename) {
        this(centerFilename, centerFilename, topFilename, bottomFilename, centerFilename, centerFilename);
    }
    
    public Skybox(String rightFilename, String leftFilename, String topFilename, String bottomFilename, String frontFilename, String backFilename) {
        Map<Integer, String> images = new HashMap<>();
        
        for(int i = 0; i < 6; i++) {
            switch(i) {
                case 0 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_X, rightFilename);
                case 1 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, leftFilename);
                case 2 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, topFilename);
                case 3 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, bottomFilename);
                case 4 -> images.put(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, frontFilename);
                case 5 -> images.put(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, backFilename);
            }
        }
        
        cubemap = new Cubemap(images);
        g       = new Graphics();
        
        g.vertices = MemoryUtil.memAllocFloat(192);
        g.indices  = MemoryUtil.memAllocInt(36);
        
        //Front
        g.vertices.put(-1) .put(1).put(-1); //0
        g.vertices .put(1) .put(1).put(-1); //1
        g.vertices .put(1).put(-1).put(-1); //2
        g.vertices.put(-1).put(-1).put(-1); //3
        
        //Back
        g.vertices .put(1) .put(1).put(1);  //4
        g.vertices.put(-1) .put(1).put(1);  //5
        g.vertices.put(-1).put(-1).put(1);  //6
        g.vertices .put(1).put(-1).put(1);  //7
        
        //Top
        g.vertices.put(-1).put(1) .put(1);  //8
        g.vertices .put(1).put(1) .put(1);  //9
        g.vertices .put(1).put(1).put(-1);  //10
        g.vertices.put(-1).put(1).put(-1);  //11
        
        //Bottom
        g.vertices.put(-1).put(-1).put(-1); //12
        g.vertices .put(1).put(-1).put(-1); //13
        g.vertices .put(1).put(-1) .put(1); //14
        g.vertices.put(-1).put(-1) .put(1); //15
        
        //Left
        g.vertices.put(-1) .put(1) .put(1); //16
        g.vertices.put(-1) .put(1).put(-1); //17
        g.vertices.put(-1).put(-1).put(-1); //18
        g.vertices.put(-1).put(-1) .put(1); //19
        
        //Right
        g.vertices.put(1) .put(1).put(-1);  //20
        g.vertices.put(1) .put(1) .put(1);  //21
        g.vertices.put(1).put(-1) .put(1);  //22
        g.vertices.put(1).put(-1).put(-1);  //23
        
        g.indices.put(0).put(1).put(2).put(2).put(3).put(0);       //Front
        g.indices.put(4).put(5).put(6).put(6).put(7).put(4);       //Back
        g.indices.put(8).put(9).put(10).put(10).put(11).put(8);    //Top
        g.indices.put(12).put(13).put(14).put(14).put(15).put(12); //Bottom
        g.indices.put(16).put(17).put(18).put(18).put(19).put(16); //Left
        g.indices.put(20).put(21).put(22).put(22).put(23).put(20); //Right
        
        g.vertices.flip();
        g.indices.flip();
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
    }
    
    void render(Matrix4f viewMatrix) {
        XJGE.getDefaultGLProgram().use();
        
        glDepthMask(false);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemap.handle);
        glBindVertexArray(g.vao);
        
        viewMatrix.get3x3(tempView);
        newView.set(tempView);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 8);
        XJGE.getDefaultGLProgram().setUniform("uView", false, newView);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDepthMask(true);
        
        XJGE.getDefaultGLProgram().setUniform("uView", false, viewMatrix);
        
        ErrorUtils.checkGLError();
    }
    
    public void freeResources() {
        cubemap.freeTexture();
        g.freeBuffers();
    }
    
}