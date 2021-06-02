package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.graphics.Color;
import java.io.InputStream;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Jun 1, 2021
 */

public final class Font2 {

    public static int DEFAULT_SIZE = 32;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    public Font2(String filename, int size) {
        
    }
    
    private void loadFont(InputStream file, int size) {
        
    }
    
    public void drawString(String text, Vector3f position, Color color) {
        
    }
    
}