package org.xjge.test;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Font;
import org.xjge.core.Split;
import org.xjge.core.StopWatch;
import org.xjge.core.Widget;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;

/**
 * Created: Apr 25, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestWidget extends Widget {

    Font font = new Font("font_source_code_pro.ttf", 32);
    StopWatch timer = new StopWatch();
    
    int index;
    boolean reverse;
    
    public TestWidget() {
        super(new Vector3i(), Window.getWidth(), Window.getHeight());
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        /*
        TODO:
        - Test variable string lengths
        - Profile efficency/garbage creation
        - Impose maximum char count
        - Add .bmf parsing
        */
        
        if(timer.tick(1)) {
            if(!reverse) {
                index++;
            } else {
                index--;
            }
            
            if(index > 44) {
                index = 44;
                reverse = !reverse;
            } else if(index < 0) {
                index = 0;
                reverse = !reverse;
            }
        }
        
        font.drawString("The quick brown fox jumps over the lazy dog.".substring(0, index), 50, 100, Color.WHITE, 1f);
        
        /*
        font2.drawString(" !\"#$%&\'()*+,-./", 10, 120, Color.WHITE, 1f);
        font2.drawString("0123456789:;<=>?", 10, 100, Color.WHITE, 1f);
        font2.drawString("@ABCDEFGHIJKLMNO", 10, 80, Color.WHITE, 1f);
        font2.drawString("PQRSTUVWXYZ[\\]^_", 10, 60, Color.WHITE, 1f);
        font2.drawString("`abcdefghijklmno", 10, 40, Color.WHITE, 1f);
        font2.drawString("pqrstuvwxyz{|}~", 10, 20, Color.WHITE, 1f);
        */
        
        //font3.drawString("The quick brown fox jumps over the lazy dog. 1234567890 !@#$%^&*()", 10, 60, Color.WHITE, 1f);
    }

    @Override
    public void relocate(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(double cursorPosX, double cursorPosY, int button, int action, int mods, double scrollX, double scrollY) {
    }

    @Override
    public void destroy() {
    }

}