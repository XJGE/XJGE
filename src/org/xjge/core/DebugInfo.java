package org.xjge.core;

import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Icon;
import org.xjge.ui.RectangleBatch;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.graphics.Color;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_5;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.xjge.ui.Font.placeholder;
import org.xjge.graphics.Texture;

/**
 * Created: Jun 10, 2021
 * <p>
 * Provides users with a diagnostic interface useful for debugging at runtime. 
 * This object is rendered independent of player {@link Viewport}.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class DebugInfo {
    
    private final int PADDING = 8;
    
    boolean show;
    
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
    DebugInfo(Texture iconTexture) {
        for(int r = 0; r < rectangles.length; r++) rectangles[r] = new Rectangle();
        for(int p = 0; p < textPos.length; p++)    textPos[p] = new Vector2i();
        //for(int i = 0; i < icons.length; i++)      icons[i] = new Icon(iconTexture, 64, 64);
        
        icons[4].setSubImage(0, 1);
    }
    
    /**
     * Reconfigures the positions of each group in the interface to better fit 
     * the dimensions of the window.
     */
    void updatePosition() {
        //Group 1: Runtime Info
        {
            rectangles[0].width     = 320 + (PADDING * 2);
            rectangles[0].height    = (Font.DEFAULT_FONT_SIZE * 4) + PADDING;
            rectangles[0].positionY = Window.getHeight() - rectangles[0].height;
            
            for(int i = 0; i < 4; i++) {
                textPos[i].set(PADDING, Window.getHeight() - (Font.DEFAULT_FONT_SIZE * (i + 1)));
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
            
            rectangles[1].width     = placeholder.lengthInPixels(longestString) + (PADDING * 2);
            rectangles[1].height    = (Font.DEFAULT_FONT_SIZE * 3) + PADDING;
            rectangles[1].positionX = Window.getWidth() - rectangles[1].width;
            rectangles[1].positionY = Window.getHeight() - rectangles[1].height;
            
            for(int i = 4; i < 7; i++) {
                textPos[i].set(rectangles[1].positionX + PADDING, Window.getHeight() - (Font.DEFAULT_FONT_SIZE * (i - 3)));
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
            
            rectangles[2].width     = placeholder.lengthInPixels(longestString) + (PADDING * 2);
            rectangles[2].height    = (Font.DEFAULT_FONT_SIZE * 4) + PADDING;
            rectangles[2].positionX = Window.getWidth() - rectangles[2].width;
            rectangles[2].positionY = rectangles[1].positionY - (rectangles[2].height + PADDING);
            
            for(int i = 7; i < 11; i++) {
                textPos[i].set(rectangles[2].positionX + PADDING, 
                              (rectangles[1].positionY - PADDING) - (Font.DEFAULT_FONT_SIZE * (i - 6)));
            }
        }
        
        //Group 4: Graphics Info
        {
            String[] strings     = new String[4];
            String longestString = "";
            
            strings[0] = "OPENGL VER: " + glGetString(GL_VERSION);
            strings[1] = "GFX CARD: " + glGetString(GL_RENDERER);
            strings[2] = "DISPLAY: \"" + Window.getMonitor().name + "\" (" + Window.getMonitor().getInfo() + ")";
            strings[3] = "VSYNC: " + Hardware.getVSyncEnabled();
            
            for(String string : strings) {
                if(string.length() > longestString.length()) longestString = string;
            }
            
            rectangles[3].width     = placeholder.lengthInPixels(longestString) + (PADDING * 2);
            rectangles[3].height    = (Font.DEFAULT_FONT_SIZE * 4) + PADDING;
            rectangles[3].positionX = Window.getWidth() - rectangles[3].width;
            rectangles[3].positionY = rectangles[2].positionY - (rectangles[3].height + PADDING);
            
            for(int i = 11; i < 15; i++) {
                textPos[i].set(rectangles[3].positionX + PADDING, 
                              (rectangles[2].positionY - PADDING) - (Font.DEFAULT_FONT_SIZE * (i - 10)));
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
            
            int length = placeholder.lengthInPixels(longestString) + (64 + (PADDING * 2));
            
            rectangles[4].width     = (length > rectangles[0].width) ? length : rectangles[0].width;
            rectangles[4].height    = 252;
            rectangles[4].positionY = rectangles[0].positionY - (rectangles[4].height + PADDING);
            
            for(int i = 0; i < icons.length - 1; i++) {
                float yPos = (rectangles[4].positionY + rectangles[4].height) - ((48 * (i + 1)) + 52);
                //icons[i].setPosition(PADDING, (int) yPos);
            }
            
            //icons[4].setPosition(PADDING, rectangles[4].positionY + rectangles[4].height - 48);
            textPos[15].set(80, rectangles[4].positionY + rectangles[4].height - 36);
            
            for(int i = 16; i < textPos.length - 1; i += 2) {
                float yPos = (rectangles[4].positionY + rectangles[4].height) - (96 + PADDING);
                int index  = (i % 16) / 2;
                
                textPos[i].set(60, (int) (yPos - (48 * index)));
                textPos[i + 1].set(80, (int) ((yPos + 16) - (48 * index)));
            }
        }
        
        //Group 6: Engine Info
        {
            rectangles[5].width     = placeholder.lengthInPixels("XJGE v" + XJGE.VERSION) + (PADDING * 2);
            rectangles[5].height    = 24 + PADDING;
            rectangles[5].positionX = Window.getWidth() - rectangles[5].width;
            rectangles[5].positionY = rectangles[3].positionY - (rectangles[5].height + PADDING);
            
            textPos[24].set(rectangles[5].positionX + PADDING, rectangles[5].positionY + PADDING);
        }
    }
    
    /**
     * Renders the interface to the window.
     */
    void render() {
        rectBatch.batchStart();
            for(Rectangle rectangle : rectangles) rectBatch.drawRectangle(rectangle, Color.BLACK, 0.5f);
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
        
        //This class is legacy, set FPS, Delta, and Ticked values to 0 to avoid errors
        placeholder.drawString("FPS: " + 0, textPos[0].x, textPos[0].y, Color.WHITE, 1f);
        placeholder.drawString("DELTA: " + 0, textPos[1].x, textPos[1].y, Color.WHITE, 1f);
        placeholder.drawString("TICKED: " + 0, textPos[2].x, textPos[2].y, Color.WHITE, 1f);
        placeholder.drawString("MEM FREE: " + Runtime.getRuntime().freeMemory(), textPos[3].x, textPos[3].y, Color.CYAN, 1f);
        
        placeholder.drawString("OS NAME: " + System.getProperty("os.name"), textPos[4].x, textPos[4].y,  Color.GREEN, 1f);
        placeholder.drawString("JAVA VER: " + System.getProperty("java.version"), textPos[5].x, textPos[5].y,  Color.GREEN, 1f);
        placeholder.drawString("GLFW VER: " + glfwGetVersionString(), textPos[6].x, textPos[6].y, Color.GREEN, 1f);
        
        placeholder.drawString("OPENAL VER: " + alGetString(AL_VERSION), textPos[7].x, textPos[6].y, Color.WHITE, 1f);
        placeholder.drawString("SPEAKER: \"" + Audio.speaker.name.substring(15) + "\"", textPos[8].x, textPos[8].y, Color.YELLOW, 1f);
        placeholder.drawString("SOUND MASTER VOLUME: " + Audio.getSoundMasterVolume(), textPos[9].x, textPos[9].y, Color.CYAN, 1f);
        placeholder.drawString("MUSIC MASTER VOLUME: " + Audio.getMusicMasterVolume(), textPos[10].x, textPos[10].y, Color.CYAN, 1f);
        
        placeholder.drawString("OPENGL VER: " + glGetString(GL_VERSION), textPos[11].x, textPos[11].y, Color.WHITE, 1f);
        placeholder.drawString("GFX CARD: " + glGetString(GL_RENDERER), textPos[12].x, textPos[11].y, Color.YELLOW, 1f);
        placeholder.drawString("DISPLAY: \"" + Window.getMonitor().name + "\" (" + Window.getMonitor().getInfo() + ")", 
                               textPos[13].x, textPos[13].y, Color.YELLOW, 1f);
        placeholder.drawString("VSYNC: " + Hardware.getVSyncEnabled(), textPos[14].x, textPos[14].y, Color.CYAN, 1f);
        
        placeholder.drawString(Input.getDeviceEnabled(KEY_MOUSE_COMBO) ? "Enabled" : "Disabled", 
                               textPos[15].x, textPos[15].y, Color.WHITE, 1f);
        
        for(int i = 16; i < textPos.length - 1; i += 2) {
            int jid = (i % 16) / 2;
            
            placeholder.drawString(jid + "", textPos[i].x, textPos[i].y, Color.YELLOW, 1f);
            placeholder.drawString(Input.getDevicePresent(jid) ? Input.getDeviceName(jid) : "N/A", 
                                   textPos[i + 1].x, textPos[i + 1].y, Color.WHITE, 1f);
        }
        
        placeholder.drawString("XJGE v" + XJGE.VERSION, textPos[24].x, textPos[14].y, Color.MAGENTA, 1f);
    }

    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    void freeBuffers() {
        rectBatch.freeBuffers();
    }
    
}