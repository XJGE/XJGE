package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;

/**
 * @author J Hoffman
 * Created: Jun 10, 2021
 */

final class DebugInfo {
    
    private final int PADDING = 8;
    
    boolean show;
    
    private final Font font;
    private final Text text = new Text();
    
    private final RectangleBatch rectBatch = new RectangleBatch(3);
    
    private final Rectangle[] rectangles = new Rectangle[3];
    private final Vector3f[] textPos     = new Vector3f[20];
    
    DebugInfo(Font font, Texture iconTexture) {
        this.font = font;
        
        for(int r = 0; r < rectangles.length; r++) rectangles[r] = new Rectangle();
        for(int p = 0; p < textPos.length; p++)    textPos[p] = new Vector3f();
    }
    
    void updatePosition() {
        //Group 1: runtime info.
        {
            rectangles[0].width  = 320;
            rectangles[0].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[0].yPos   = Window.getHeight() - rectangles[0].height;
            
            for(int i = 0; i < 4; i++) {
                textPos[i].set(PADDING / 2, Window.getHeight() - (Font.DEFAULT_SIZE * (i + 1)), 0);
            }
        }
        
        //Group 2: system info.
        {
            rectangles[1].width  = Text.lengthInPixels("GLFW: " + glfwGetVersionString(), font) + PADDING;
            rectangles[1].height = (Font.DEFAULT_SIZE * 7) + PADDING;
            rectangles[1].xPos   = Window.getWidth() - rectangles[1].width;
            rectangles[1].yPos   = Window.getHeight() - rectangles[1].height;
            
        }
        
        //Group 3: input info.
        {
            rectangles[2].width  = 320;
            rectangles[2].height = 400;
            rectangles[2].yPos   = (Window.getHeight() - rectangles[2].height) - (rectangles[0].height + PADDING) ;
        }    
    }
    
    void render() {
        rectBatch.batchStart(0.4f);
            for(Rectangle rectangle : rectangles) rectBatch.drawRectangle(rectangle, Color.RED);
        rectBatch.batchEnd();
        
        text.drawString(font, "FPS: " + Game.getFPS(),                          textPos[0], Color.WHITE);
        text.drawString(font, "DELTA: " + Game.getDelta(),                      textPos[1], Color.WHITE);
        text.drawString(font, "TICKED: " + Game.getTicked(),                    textPos[2], Color.WHITE);
        text.drawString(font, "MEM FREE: " + Runtime.getRuntime().freeMemory(), textPos[3], Color.CYAN);
        
        
        
        text.resetStringIndex();
    }

    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}