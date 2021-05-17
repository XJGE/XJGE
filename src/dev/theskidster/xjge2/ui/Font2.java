package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.Window;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.graphics.Instance2D;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

public final class Font2 {

    private final int vboPosOffset = glGenBuffers();
    private final int vboTexOffset = glGenBuffers();
    private final int vboColOffset = glGenBuffers();
    
    private Instance2D data;
    
    private HashMap<Character, Vector2i> posOffsets = new HashMap<>();
    private HashMap<Character, Vector2f> texOffsets = new HashMap<>();
    
    private int texHandle;
    private Graphics g;
    
    //https://javadoc.lwjgl.org/org/lwjgl/stb/STBTruetype.html
    
    public Font2(String filename) {
        try(InputStream file = Font2.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
            loadFont(file);
        } catch(Exception e) {
            Logger.setDomain("ui");
            Logger.logWarning("Failed to load font \"" + filename + "\"", e);
            Logger.setDomain(null);
        }
    }
    
    private void loadFont(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer fontBuf     = MemoryUtil.memAlloc(data.length).put(data).flip();
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            
            if(!stbtt_InitFont(fontInfo, fontBuf)) {
                throw new IllegalStateException("Could not parse font information.");
            }
            
            //not sure if ill need these
            IntBuffer ascentBuf  = stack.mallocInt(1);
            IntBuffer descentBuf = stack.mallocInt(1);
            IntBuffer lineGapBuf = stack.mallocInt(1);
            
            stbtt_GetFontVMetrics(fontInfo, ascentBuf, descentBuf, lineGapBuf);
            
            STBTTBakedChar.Buffer charBuf = STBTTBakedChar.malloc(96); //why 96?
            
            FloatBuffer scaleXBuf = stack.mallocFloat(1);
            FloatBuffer scaleYBuf = stack.mallocFloat(1);
            
            glfwGetMonitorContentScale(Window.getMonitor().handle, scaleXBuf, scaleYBuf);
            
            float scaleX = scaleXBuf.get();
            float scaleY = scaleYBuf.get();
            
            //TODO: maybe parse a fonts desired texture atlas size if possible?
            int desiredSize = 144;
            
            int imageWidth  = Math.round(desiredSize * scaleX);
            int imageHeight = Math.round(desiredSize * scaleY);
            
            //ByteBuffer imageBuf = BufferUtils.createByteBuffer(imageWidth * imageHeight); //why bufferutils?
            
            ByteBuffer imageBuf = MemoryUtil.memAlloc(imageWidth * imageWidth);
            
            int status = stbtt_BakeFontBitmap(fontBuf, 24 * scaleY, imageBuf, imageWidth, imageHeight, 32, charBuf);
            
            //System.out.println("s" + imageWidth);
            
            //System.out.println(Math.abs(status) - 96);
            
            //TODO: create atlas from baked bitmap- display just like we did in the legacy version
            
            texHandle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, imageWidth, imageHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            
            g = new Graphics();
            
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            g.vertices.put(-imageWidth) .put(imageHeight).put(0)  .put(0).put(0);
            g.vertices .put(imageWidth) .put(imageHeight).put(0)  .put(1).put(0);
            g.vertices .put(imageWidth).put(-imageHeight).put(0)  .put(1).put(1);
            g.vertices.put(-imageWidth).put(-imageHeight).put(0)  .put(0).put(1);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
            
            g.bindBuffers();
        
            glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(2);
            
        } catch(IOException e) {
            
        }
    }
    
    private Vector3f pos = new Vector3f(0, -300, -1000);
    
    public void update() {
        g.modelMatrix.translation(pos);
    }
    
    public void render(GLProgram program) {
        program.use();
        
        glBindTexture(GL_TEXTURE_2D, texHandle);
        glBindVertexArray(g.vao);
        
        program.setUniform("uType", 2);
        program.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        ErrorUtils.checkGLError();
    }
    
}