package org.xjge.main;

import java.util.Map;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Rectangle;

/**
 *
 * @author thesk
 */
public class TestWidget extends Widget {
    
    Rectangle[] shapes = new Rectangle[16];
    
    TestWidget() {
        super(new Vector3i(40, 60, 0), 400, 400);
        
        for(int i = 0; i < shapes.length; i++) {
            shapes[i] = new Rectangle(position.x + (16 * i), 100, 16, 16);
        }
    }
    
    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        for(int i = 0; i < shapes.length; i++) {
            Color color = switch(i) {
                default -> Color.WHITE;
                case 1 -> Color.SILVER;
                case 2 -> Color.GRAY;
                case 3 -> Color.BLACK;
                case 4 -> Color.RED;
                case 5 -> Color.ORANGE;
                case 6 -> Color.YELLOW;
                case 7 -> Color.LIME;
                case 8 -> Color.GREEN;
                case 9 -> Color.TEAL;
                case 10 -> Color.CYAN;
                case 11 -> Color.BLUE;
                case 12 -> Color.NAVY;
                case 13 -> Color.PURPLE;
                case 14 -> Color.MAGENTA;
                case 15 -> Color.BROWN;
            };
            
            shapes[i].render(1, color);
        }
    }

    @Override
    public void setSplitPosition(Split split, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyInput(int key, int action, int mods) {
    }

    @Override
    public void destroy() {
    }

}
