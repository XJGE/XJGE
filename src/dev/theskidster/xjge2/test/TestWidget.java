package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Font2;
import static dev.theskidster.xjge2.core.Font2.DEFAULT_SIZE;
import dev.theskidster.xjge2.core.Text;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.core.Widget;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: May 30, 2021
 */

public class TestWidget extends Widget {
    
    private final Vector3f textPos  = new Vector3f(100, 300, 0);
    private final Vector3f textPos2 = new Vector3f(40, 70, 0);
    private final Vector3f textPos3 = new Vector3f(40, 40, 0);
    
    private String text1;
    private String text2 = "bleh2";
    private String text3 = "asdf";
    
    private Font2 font1;
    private Font2 font2;
    
    private boolean show1 = true;
    private boolean show2 = true;
    private boolean show3 = true;
    
    private Color color1 = Color.BLACK;
    private Color color2 = Color.WHITE;
    private Color color3 = Color.BLACK;
    
    public TestWidget() {
        super(new Vector3i(), 0, 0);
        
        font1 = new Font2("fnt_debug_mono.ttf", DEFAULT_SIZE);
        font2 = new Font2("fnt_goblin_regular.ttf", DEFAULT_SIZE);
        
        text1 = Text.wrap("the quick brown fox jumped over the lazy dog.", 200, font1);
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        if(show1) drawString(font1, text1, textPos, color1);
        if(show2) drawString(font2, text2, textPos2, color2);
        if(show3) drawString(font1, text3, textPos3, color3);
    }

    @Override
    public void setSplitPosition() {
        
    }
    
    public void processKeyInput(int key, int action, int mods) {
        if(action == GLFW_PRESS) {
            switch(key) {
                case GLFW_KEY_1 -> {
                    show1 = !show1;
                }
                
                case GLFW_KEY_2 -> {
                    show2 = !show2;
                }
                
                case GLFW_KEY_3 -> {
                    show3 = !show3;
                }
                
                case GLFW_KEY_4 -> {
                    color3 = Color.GREEN;
                    //font1 = new Font2("fnt_pattaya_regular.ttf", DEFAULT_SIZE);
                }
            }
        }
    }

}