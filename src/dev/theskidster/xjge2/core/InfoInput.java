package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Icon;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;

/**
 * @author J Hoffman
 * Created: May 27, 2021
 */

final class InfoInput {
    
    private final int PADDING = 8;
    
    private final int rectWidth;
    private final int rectHeight;
    
    private final Font font;
    private final Rectangle rectangle;
    private final Text text                = new Text();
    private final RectangleBatch rectBatch = new RectangleBatch(1);
    
    private final Icon[] icons       = new Icon[5];
    private final Vector3f[] textPos = new Vector3f[9];
    
    InfoInput(Font font, Texture iconTexture) {
        this.font = font;
        
        String longestName = "N/A";
        
        for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
            if(Input.getDevicePresent(i)) {
                if(Input.getDeviceName(i).length() > longestName.length()) {
                    longestName = Input.getDeviceName(i);
                }
            }
        }
        
        rectWidth  = (Text.lengthInPixels(longestName, font)) + PADDING;
        rectHeight = 200;
        rectangle  = new Rectangle(0, Window.getHeight() - rectHeight, rectWidth, rectHeight);
        
        for(int i = 0; i < icons.length; i++) {
            float yPos = Window.getHeight() - ((48 * (i + 1)) + 52);
            
            icons[i] = new Icon(iconTexture, 64, 64);
            icons[i].setPosition(PADDING, yPos, 0);
        }
        
        icons[4].setSubImage(0, 1);
        icons[4].setPosition(PADDING, Window.getHeight() - 48, 0);
        
        for(int i = 0; i < textPos.length; i++) {
            float yPos = Window.getHeight() - (42 * (i + 1));
            textPos[i] = new Vector3f(64 + (PADDING * 2), yPos, 0);
        }
    }
    
    void updatePosition() {
        
    }
    
    void render() {
        for(int i = 0; i < icons.length; i++) {
            if(i != icons.length - 1) {
                if(Input.getDevicePresent(i)) {
                    if(Input.getDeviceEnabled(i)) icons[i].setSubImage(2, 0);
                    else                          icons[i].setSubImage(1, 0);
                } else {
                    icons[i].setSubImage(0, 0);
                }
            }
            
            icons[i].render();
        }
        
        text.drawString(font, "bleh", textPos[0], Color.WHITE);
        text.drawString(font, "bleh", textPos[1], Color.WHITE);
        text.drawString(font, "bleh", textPos[2], Color.WHITE);
        text.drawString(font, "bleh", textPos[3], Color.WHITE);
        text.drawString(font, "bleh", textPos[4], Color.WHITE);
        text.drawString(font, "bleh", textPos[5], Color.WHITE);
        text.drawString(font, "bleh", textPos[6], Color.WHITE);
        text.drawString(font, "bleh", textPos[7], Color.WHITE);
        text.drawString(font, "bleh", textPos[8], Color.WHITE);
        
        text.resetStringIndex();
    }
    
    void freeBuffers() {
        
    }
    
}