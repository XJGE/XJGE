package org.xjge.core;

import org.xjge.graphics.Rectangle;
import org.xjge.graphics.Icon;
import org.xjge.graphics.RectangleBatch;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.graphics.Color;
import org.xjge.graphics.Texture;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

//Created: Jun 10, 2021

/**
 * Provides users with a diagnostic interface useful for debugging at runtime. 
 * This object is rendered independent of player {@link Viewport}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class DebugInfo {
    
    private final int PADDING = 8;
    
    boolean show;
    
    private final Font font;
    private final Text text = new Text();
    
    private final RectangleBatch rectBatch = new RectangleBatch(6);
    
    private final Rectangle[] rectangles = new Rectangle[6];
    private final Vector2i[] textPos     = new Vector2i[25];
    private final Icon[] icons           = new Icon[5];
    
    /**
     * Creates a new debug interface using the supplied font and texture.
     * 
     * @param font the font supplied by the engine that the interface will use
     * @param iconTexture the texture containing the various icons in the interface
     */
    DebugInfo(Font font, Texture iconTexture) {
        this.font = font;
        
        for(int r = 0; r < rectangles.length; r++) rectangles[r] = new Rectangle();
        for(int p = 0; p < textPos.length; p++)    textPos[p] = new Vector2i();
        for(int i = 0; i < icons.length; i++)      icons[i] = new Icon(iconTexture, 64, 64);
        
        icons[4].setSubImage(0, 1);
    }
    
    /**
     * Reconfigures the positions of each group in the interface to better fit 
     * the dimensions of the window.
     */
    void updatePosition() {
        //Group 1: Runtime Info
        {
            rectangles[0].width  = 320 + (PADDING * 2);
            rectangles[0].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[0].yPos   = Window.getHeight() - rectangles[0].height;
            
            for(int i = 0; i < 4; i++) {
                textPos[i].set(PADDING, Window.getHeight() - (Font.DEFAULT_SIZE * (i + 1)));
            }
        }
        
        //Group 2: System Info
        {
            String[] strings     = new String[3];
            String longestString = "";
            
            strings[0] = "OS NAME: " + System.getProperty("os.name");
            strings[1] = "JAVA VER: " + System.getProperty("java.version");
            strings[2] = "GLFW VER: " + glfwGetVersionString();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[1].width  = Text.lengthInPixels(longestString, font) + (PADDING * 2);
            rectangles[1].height = (Font.DEFAULT_SIZE * 3) + PADDING;
            rectangles[1].xPos   = Window.getWidth() - rectangles[1].width;
            rectangles[1].yPos   = Window.getHeight() - rectangles[1].height;
            
            for(int i = 4; i < 7; i++) {
                textPos[i].set(rectangles[1].xPos + PADDING, Window.getHeight() - (Font.DEFAULT_SIZE * (i - 3)));
            }
        }
        
        //Group 3: Audio Info
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            strings[0] = "OPENAL VER: " + alGetString(AL_VERSION);
            strings[1] = "SPEAKER: \"" + Audio.speaker.name.substring(15) + "\"";
            strings[2] = "SOUND MASTER VOLUME: " + Audio.getSoundMasterVolume();
            strings[3] = "MUSIC MASTER VOLUME: " + Audio.getMusicMasterVolume();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[2].width  = Text.lengthInPixels(longestString, font) + (PADDING * 2);
            rectangles[2].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[2].xPos   = Window.getWidth() - rectangles[2].width;
            rectangles[2].yPos   = rectangles[1].yPos - (rectangles[2].height + PADDING);
            
            for(int i = 7; i < 11; i++) {
                textPos[i].set(rectangles[2].xPos + PADDING, 
                              (rectangles[1].yPos - PADDING) - (Font.DEFAULT_SIZE * (i - 6)));
            }
        }
        
        //Group 4: Graphics Info
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            strings[0] = "OPENGL VER: " + glGetString(GL_VERSION);
            strings[1] = "GFX CARD: " + glGetString(GL_RENDERER);
            strings[2] = "DISPLAY: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")";
            strings[3] = "VSYNC: " + Hardware.getVSyncEnabled();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[3].width  = Text.lengthInPixels(longestString, font) + (PADDING * 2);
            rectangles[3].height = (Font.DEFAULT_SIZE * 4) + PADDING;
            rectangles[3].xPos   = Window.getWidth() - rectangles[3].width;
            rectangles[3].yPos   = rectangles[2].yPos - (rectangles[3].height + PADDING);
            
            for(int i = 11; i < 15; i++) {
                textPos[i].set(rectangles[3].xPos + PADDING, 
                              (rectangles[2].yPos - PADDING) - (Font.DEFAULT_SIZE * (i - 10)));
            }
        }
        
        //Group 5: Input Info
        {
            String longestString = "N/A";
            
            for(int i = 0; i < GLFW_JOYSTICK_5; i++) {
                if(Input.getDevicePresent(i)) {
                    if(Input.getDeviceName(i).length() > longestString.length()) {
                        longestString = Input.getDeviceName(i);
                    }
                }
            }
            
            int length = Text.lengthInPixels(longestString, font) + (64 + (PADDING * 2));
            
            rectangles[4].width  = (length > rectangles[0].width) ? length : rectangles[0].width;
            rectangles[4].height = 252;
            rectangles[4].yPos   = rectangles[0].yPos - (rectangles[4].height + PADDING);
            
            for(int i = 0; i < icons.length - 1; i++) {
                float yPos = (rectangles[4].yPos + rectangles[4].height) - ((48 * (i + 1)) + 52);
                icons[i].setPosition(PADDING, (int) yPos);
            }
            
            icons[4].setPosition(PADDING, rectangles[4].yPos + rectangles[4].height - 48);
            textPos[15].set(80, rectangles[4].yPos + rectangles[4].height - 36);
            
            for(int i = 16; i < textPos.length - 1; i += 2) {
                float yPos = (rectangles[4].yPos + rectangles[4].height) - (96 + PADDING);
                int index  = (i % 16) / 2;
                
                textPos[i].set(60, (int) (yPos - (48 * index)));
                textPos[i + 1].set(80, (int) ((yPos + 16) - (48 * index)));
            }
        }
        
        //Group 6: Engine Info
        {
            rectangles[5].width  = Text.lengthInPixels("XJGE v" + XJGE.VERSION, font) + (PADDING * 2);
            rectangles[5].height = 24 + PADDING;
            rectangles[5].xPos   = Window.getWidth() - rectangles[5].width;
            rectangles[5].yPos   = rectangles[3].yPos - (rectangles[5].height + PADDING);
            
            textPos[24].set(rectangles[5].xPos + PADDING, rectangles[5].yPos + PADDING);
        }
    }
    
    /**
     * Renders the interface to the window.
     */
    void render() {
        rectBatch.batchStart(0.5f);
            for(Rectangle rectangle : rectangles) rectBatch.drawRectangle(rectangle, Color.BLACK);
        rectBatch.batchEnd();
        
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
        
        text.drawString(font, "FPS: " + Game.getFPS(), textPos[0], Color.WHITE, 1f);
        text.drawString(font, "DELTA: " + Game.getDelta(), textPos[1], Color.WHITE, 1f);
        text.drawString(font, "TICKED: " + Game.getTicked(), textPos[2], Color.WHITE, 1f);
        text.drawString(font, "MEM FREE: " + Runtime.getRuntime().freeMemory(), textPos[3], Color.CYAN, 1f);
        
        text.drawString(font, "OS NAME: " + System.getProperty("os.name"), textPos[4],  Color.GREEN, 1f);
        text.drawString(font, "JAVA VER: " + System.getProperty("java.version"), textPos[5],  Color.GREEN, 1f);
        text.drawString(font, "GLFW VER: " + glfwGetVersionString(), textPos[6], Color.GREEN, 1f);
        
        text.drawString(font, "OPENAL VER: " + alGetString(AL_VERSION), textPos[7], Color.WHITE, 1f);
        text.drawString(font, "SPEAKER: \"" + Audio.speaker.name.substring(15) + "\"", textPos[8], Color.YELLOW, 1f);
        text.drawString(font, "SOUND MASTER VOLUME: " + Audio.getSoundMasterVolume(), textPos[9], Color.CYAN, 1f);
        text.drawString(font, "MUSIC MASTER VOLUME: " + Audio.getMusicMasterVolume(), textPos[10], Color.CYAN, 1f);
        
        text.drawString(font, "OPENGL VER: " + glGetString(GL_VERSION), textPos[11], Color.WHITE, 1f);
        text.drawString(font, "GFX CARD: " + glGetString(GL_RENDERER), textPos[12], Color.YELLOW, 1f);
        text.drawString(font, "DISPLAY: \"" + Window.monitor.name + "\" (" + Window.monitor.getInfo() + ")", textPos[13], Color.YELLOW, 1f);
        text.drawString(font, "VSYNC: " + Hardware.getVSyncEnabled(), textPos[14], Color.CYAN, 1f);
        
        text.drawString(font, Input.getDeviceEnabled(KEY_MOUSE_COMBO) ? "Enabled" : "Disabled", textPos[15], Color.WHITE, 1f);
        
        for(int i = 16; i < textPos.length - 1; i += 2) {
            int jid = (i % 16) / 2;
            
            text.drawString(font, jid + "", textPos[i], Color.YELLOW, 1f);
            text.drawString(font, Input.getDevicePresent(jid) ? Input.getDeviceName(jid) : "N/A", textPos[i + 1], Color.WHITE, 1f);
        }
        
        text.drawString(font, "XJGE v" + XJGE.VERSION, textPos[24], Color.MAGENTA, 1f);
        
        text.resetStringIndex();
    }

    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}