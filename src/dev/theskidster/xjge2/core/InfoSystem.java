package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoSystem {

    private final int PADDING = 8;
    
    private final int rectWidth;
    private final int rectHeight;
    
    private final Font font;
    private final Text text = new Text();
    private final Rectangle rectangle;
    
    private final RectangleBatch rectBatch = new RectangleBatch(1);
    
    private final Vector3f[] textPos = new Vector3f[5];
    
    InfoSystem(Font font) {
        this.font = font;
        
        rectWidth  = (Text.lengthInPixels("GLFW: " + glfwGetVersionString(), font)) + PADDING;
        rectHeight = (Font.DEFAULT_SIZE * textPos.length) + PADDING;
        rectangle  = new Rectangle(0, XJGE.getResolutionY() - rectHeight, rectWidth, rectHeight);
        
        for(int i = 0; i < textPos.length; i++) {
            float yPos = (rectangle.yPos + rectangle.height) - (Font.DEFAULT_SIZE * (i + 1));            
            textPos[i] = new Vector3f(PADDING / 2, yPos, 0);
        }
    }
    
    void updatePosition() {
        rectangle.yPos  = XJGE.getResolutionY() - rectHeight;
        rectangle.width = (Text.lengthInPixels("GLFW: " + glfwGetVersionString(), font)) + PADDING;
        
        for(int i = 0; i < textPos.length; i++) {
            textPos[i].y = (rectangle.yPos + rectangle.height) - (Font.DEFAULT_SIZE * (i + 1));
        }
    }
    
    void render() {
        rectBatch.batchStart(0.4f);
            rectBatch.drawRectangle(rectangle, Color.BLACK);
        rectBatch.batchEnd();
        
        text.drawString(font, System.getProperty("os.name"), textPos[0], Color.LIME);
        text.drawString(font, "Java " + System.getProperty("java.version"), textPos[1], Color.WHITE);
        text.drawString(font, "GLFW: " + glfwGetVersionString(), textPos[2], Color.WHITE);
        text.drawString(font, "OPENGL: " + glGetString(GL_VERSION), textPos[3], Color.WHITE);
        
        //TODO: add openal version
        //text.drawString(font, "OPENAL: " + Runtime.getRuntime().freeMemory(), textPos[4], Color.WHITE);
        
        text.resetStringIndex();
    }
    
    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}