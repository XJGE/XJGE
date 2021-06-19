package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

public abstract class Widget {

    protected int width;
    protected int height;
    
    protected static Font engineFont;
    
    private final Text text  = new Text();
    public Vector3i position = new Vector3i();
    
    public Widget(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    public abstract void update(); //TODO: cursorX/Y?
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void setSplitPosition();
    
    int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
    void setEngineFont(Font font) {
        engineFont = new Font(font);
    }
    
    protected void drawString(Font font, String text, Vector2i position, Color color) {
        this.text.drawString(font, text, position, color);
    }
    
    void resetStringIndex() {
        text.resetStringIndex();
    }
    
}