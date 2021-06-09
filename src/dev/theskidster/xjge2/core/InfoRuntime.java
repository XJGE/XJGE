package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoRuntime {
    
    private final int PADDING = 8;
    
    private final int rectWidth;
    private final int rectHeight;
    
    private final Font font;
    private final Text text = new Text();
    private final Rectangle rectangle;
    
    private final RectangleBatch rectBatch = new RectangleBatch(1);
    
    private final Vector3f[] textPos = new Vector3f[6];
    
    InfoRuntime(Font font) {
        this.font = font;
        
        rectWidth  = (Text.lengthInPixels("DISPLAY: " + Window.monitor.getInfo(), font)) + PADDING;
        rectHeight = (Font.DEFAULT_SIZE * 6) + PADDING;
        rectangle  = new Rectangle(0, XJGE.getResolutionY() - rectHeight, rectWidth, rectHeight);
        
        for(int i = 0; i < textPos.length; i++) {
            float yPos = (rectangle.yPos + rectangle.height) - (Font.DEFAULT_SIZE * (i + 1));            
            textPos[i] = new Vector3f(PADDING / 2, yPos, 0);
        }
    }
    
    void updatePosition() {
        rectangle.yPos  = XJGE.getResolutionY() - rectHeight;
        rectangle.width = (Text.lengthInPixels("DISPLAY: " + Window.monitor.getInfo(), font)) + PADDING;
        
        for(int i = 0; i < textPos.length; i++) {
            textPos[i].y = (rectangle.yPos + rectangle.height) - (Font.DEFAULT_SIZE * (i + 1));
        }
    }
    
    void render() {
        rectBatch.batchStart(0.4f);
            rectBatch.drawRectangle(rectangle, Color.BLACK);
        rectBatch.batchEnd();
        
        text.drawString(font, "FPS: " + Game.getFPS(), textPos[0], Color.WHITE);
        text.drawString(font, "DELTA: " + Game.getDelta(), textPos[1], Color.WHITE);
        text.drawString(font, "TICKED: " + Game.getTicked(), textPos[2], Color.WHITE);
        text.drawString(font, "VSYNC: " + WinKit.getVSyncEnabled(), textPos[3], Color.YELLOW);
        text.drawString(font, "DISPLAY: " + Window.monitor.getInfo(), textPos[4], Color.YELLOW);
        text.drawString(font, "MEM FREE: " + Runtime.getRuntime().freeMemory(), textPos[5], Color.CYAN);
        
        text.resetStringIndex();
    }
    
    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}