package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Font2;
import static dev.theskidster.xjge2.core.Font2.DEFAULT_SIZE;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.core.Widget;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

/**
 * @author J Hoffman
 * Created: May 30, 2021
 */

public class TestWidget extends Widget {
    
    private final Vector3f textPos  = new Vector3f(40, 100, 0);
    private final Vector3f textPos2 = new Vector3f(40, 70, 0);
    
    private final Font2 font;
    
    private LinkedHashSet<String> words   = new LinkedHashSet<>();
    private TreeMap<Integer, Character> m = new TreeMap<>();
    
    public TestWidget() {
        super(new Vector3i(), 0, 0);
        
        font = new Font2("fnt_debug_mono.ttf", DEFAULT_SIZE);
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        /*
        
        */
        
        drawString(font, "asdf", textPos, Color.BLACK);
        drawString(font, "bleh2", textPos2, Color.BLACK);
    }

    @Override
    public void setSplitPosition() {
        
    }
    
    public void processKeyInput(int key, int action, int mods) {
        if(action == GLFW_PRESS) {
            System.out.println(key + " pressed");
            //TODO: test moving drawString calls around and changing parameter values, etc.
        }
    }

}