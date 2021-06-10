package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Icon;
import dev.theskidster.xjge2.graphics.Rectangle;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * @author J Hoffman
 * Created: Jun 10, 2021
 */

final class DebugInfo {
    
    private final int PADDING = 8;
    
    boolean show;
    
    private final Font font;
    private final Text text = new Text();
    
    private final RectangleBatch rectBatch = new RectangleBatch(5);
    
    private final Rectangle[] rectangles = new Rectangle[5];
    private final Vector3f[] textPos     = new Vector3f[24];
    private final Icon[] icons           = new Icon[5];
    
    DebugInfo(Font font, Texture iconTexture) {
        this.font = font;
        
        for(int r = 0; r < rectangles.length; r++) rectangles[r] = new Rectangle();
        for(int p = 0; p < textPos.length; p++)    textPos[p] = new Vector3f();
        for(int i = 0; i < icons.length; i++)      icons[i] = new Icon(iconTexture, 64, 64);
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
            String[] strings     = new String[3];
            String longestString = "";
            
            strings[0] = "OS NAME: " + System.getProperty("os.name");
            strings[1] = "JAVA VER: " + System.getProperty("java.version");
            strings[2] = "GLFW VER: " + glfwGetVersionString();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[1].width  = Text.lengthInPixels(longestString, font) + PADDING;
            rectangles[1].height = (Font.DEFAULT_SIZE * 3) + PADDING;
            rectangles[1].xPos   = Window.getWidth() - rectangles[1].width;
            rectangles[1].yPos   = Window.getHeight() - rectangles[1].height;
            
            for(int i = 4; i < 7; i++) {
                textPos[i].set(rectangles[1].xPos + (PADDING / 2), Window.getHeight() - (Font.DEFAULT_SIZE * (i - 3)), 0);
            }
        }
        
        //Group 3: audio info.
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            strings[0] = "OPENAL VER: "; //TODO: add openal once its integrated.
            strings[1] = "SPEAKER: \"";
            strings[2] = "MUSIC MASTER VOL: ";
            strings[3] = "SOUND MASTER VOL: ";
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[2].width  = Text.lengthInPixels(longestString, font) + PADDING;
            rectangles[2].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[2].xPos   = Window.getWidth() - rectangles[2].width;
            rectangles[2].yPos   = rectangles[1].yPos - (rectangles[2].height + PADDING);
            
            for(int i = 7; i < 11; i++) {
                textPos[i].set(rectangles[2].xPos + (PADDING / 2), 
                              (rectangles[1].yPos - PADDING) - (Font.DEFAULT_SIZE * (i - 6)), 
                              0);
            }
        }
        
        //Group 4: graphics info.
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            strings[0] = "OPENGL VER: " + glGetString(GL_VERSION);
            strings[1] = "GFX CARD: " + glGetString(GL_RENDERER);
            strings[2] = "DISPLAY: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")";
            strings[3] = "VSYNC: " + WinKit.getVSyncEnabled();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[3].width  = Text.lengthInPixels(longestString, font) + PADDING;
            rectangles[3].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[3].xPos   = Window.getWidth() - rectangles[3].width;
            rectangles[3].yPos   = rectangles[2].yPos - (rectangles[3].height + PADDING);
            
            for(int i = 11; i < 15; i++) {
                textPos[i].set(rectangles[3].xPos + (PADDING / 2), 
                              (rectangles[2].yPos - PADDING) - (Font.DEFAULT_SIZE * (i - 10)), 
                              0);
            }
        }
        
        //Group 5: input info.
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            /*
            rectangles[2].width  = 320;
            rectangles[2].height = 400;
            rectangles[2].yPos   = (Window.getHeight() - rectangles[2].height) - (rectangles[0].height + PADDING);
            */
            
        }    
    }
    
    void render() {
        rectBatch.batchStart(0.4f);
            for(Rectangle rectangle : rectangles) rectBatch.drawRectangle(rectangle, Color.BLACK);
        rectBatch.batchEnd();
        
        for(Icon icon : icons) icon.render();
        
        text.drawString(font, "FPS: " + Game.getFPS(), textPos[0], Color.WHITE);
        text.drawString(font, "DELTA: " + Game.getDelta(), textPos[1], Color.WHITE);
        text.drawString(font, "TICKED: " + Game.getTicked(), textPos[2], Color.WHITE);
        text.drawString(font, "MEM FREE: " + Runtime.getRuntime().freeMemory(), textPos[3], Color.CYAN);
        
        text.drawString(font, "OS NAME: " + System.getProperty("os.name"), textPos[4],  Color.LIME);
        text.drawString(font, "JAVA VER: " + System.getProperty("java.version"), textPos[5],  Color.LIME);
        text.drawString(font, "GLFW VER: " + glfwGetVersionString(), textPos[6], Color.LIME);
        
        text.drawString(font, "OPENAL VER: ", textPos[7], Color.WHITE);
        text.drawString(font, "SPEAKER: \"", textPos[8], Color.YELLOW);
        text.drawString(font, "MUSIC MASTER VOL: ", textPos[9], Color.CYAN);
        text.drawString(font, "SOUND MASTER VOL: ", textPos[10], Color.CYAN);
        
        text.drawString(font, "OPENGL VER: " + glGetString(GL_VERSION), textPos[11], Color.WHITE);
        text.drawString(font, "GFX CARD: " + glGetString(GL_RENDERER), textPos[12], Color.YELLOW);
        text.drawString(font, "DISPLAY: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")", textPos[13], Color.YELLOW);
        text.drawString(font, "VSYNC: " + WinKit.getVSyncEnabled(), textPos[14], Color.CYAN);
        
        //text.drawString(font, "OPENGL VER: " + glGetString(GL_VERSION), textPos[8], Color.WHITE);
        //text.drawString(font, "GFX CARD: " + glGetString(GL_RENDERER), textPos[9], Color.CYAN);
        //text.drawString(font, "VSYNC: " + WinKit.getVSyncEnabled(), textPos[10], Color.YELLOW);
        //text.drawString(font, "DISPLAY: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")", textPos[11], Color.YELLOW);
        
        text.resetStringIndex();
    }

    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}