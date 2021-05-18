package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.ui.Widget;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 11, 2021
 */

final class Viewport {

    final int id;
    final int texHandle;
    int width;
    int height;
    
    boolean active;
    
    private Graphics g = new Graphics();
    Vector2i botLeft   = new Vector2i();
    Vector2i topRight  = new Vector2i();
    Camera prevCamera  = new FreeCam();
    Camera currCamera  = new FreeCam();
    
    LinkedHashMap<String, Widget> ui = new LinkedHashMap<>();
    
    Viewport(int id) {
        this.id = id;
        
        width     = XJGE.getResolutionX();
        height    = XJGE.getResolutionY();
        texHandle = glGenTextures();
        
        createTextureAttachment();
        
        active = (id == 0);
    }
    
    Viewport(Viewport viewport) {
        id         = viewport.id;
        texHandle  = viewport.texHandle;
        width      = viewport.width;
        height     = viewport.height;
        active     = viewport.active;
        botLeft    = viewport.botLeft;
        topRight   = viewport.topRight;
        g          = viewport.g;
        prevCamera = viewport.prevCamera;
        currCamera = viewport.currCamera;
        //TODO: add ui
    }
    
    private void createTextureAttachment() {
        glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(0)    .put(height) .put(0)  .put(1).put(1);
            g.vertices.put(width).put(height) .put(0)  .put(0).put(1);
            g.vertices.put(width).put(0)      .put(0)  .put(0).put(0);
            g.vertices.put(0)    .put(0)      .put(0)  .put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(3).put(2).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    void render(Map<String, GLProgram> glPrograms, String stage) {
        switch(stage) {
            case "camera" -> {
                currCamera.render(glPrograms);
            }
            
            case "ui" -> {
                currCamera.setOrtho(glPrograms.get("default"), width, height);
                ui.values().forEach(widget -> widget.render(glPrograms));
                resetCamera(glPrograms);
            }
            
            case "texture" -> {
                glBindTexture(GL_TEXTURE_2D, texHandle);
                glBindVertexArray(g.vao);
                
                glPrograms.get("default").use();
                glPrograms.get("default").setUniform("uType", 0);
                
                glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
                ErrorUtils.checkGLError();
            }
        }
    }
    
    void resetCamera(Map<String, GLProgram> glPrograms) {
        XJGE.glPrograms.values().forEach(glProgram -> {
            if(currCamera.ortho) currCamera.setOrtho(glProgram, width, height);
            else                 currCamera.setPerspective(glProgram, width, height);
        });
    }
    
    void setBounds(int width, int height, int x1, int y1, int x2, int y2) {
        this.width  = width;
        this.height = height;
        botLeft.set(x1, y1);
        topRight.set(x2, y2);
        
        createTextureAttachment();
        
        ui.values().forEach(widget -> widget.setSplitPosition());
    }
    
    void addUIWidget(String name, Widget component) {
        ui.put(name, component);
        
        List<Map.Entry<String, Widget>> compList = new LinkedList<>(ui.entrySet());
        
        Collections.sort(compList, (var o1, var o2) -> {
            return (o2.getValue()).compareTo(o1.getValue());
        });
        
        var temp = new LinkedHashMap<String, Widget>();
        compList.forEach(comp2 -> temp.put(comp2.getKey(), comp2.getValue()));
        
        ui.clear();
        ui.putAll(temp);
    }
    
    void removeUIWidget(String name) {
        ui.remove(name);
    }
    
}