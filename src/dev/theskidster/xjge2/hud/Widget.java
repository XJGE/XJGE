package dev.theskidster.xjge2.hud;

import dev.theskidster.xjge2.core.Command;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.util.Map;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 12, 2021
 */

public abstract class Widget {

    protected int width;
    protected int height;
    
    public Vector3i position = new Vector3i();
    
    public Widget(Vector3i position, int width, int height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
    public abstract Command update();
    
    public abstract void render(Map<String, GLProgram> glPrograms);
    
    public abstract void setSplitPosition();
    
    public int compareTo(Widget widget) {
        return this.position.z - widget.position.z;
    }
    
}