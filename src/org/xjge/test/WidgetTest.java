package org.xjge.test;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.ui.Font;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Timer;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Polygon;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * Created: Apr 25, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class WidgetTest extends Widget {
    
    Font font = new Font("font_source_code_pro.ttf", 32);
    Timer timer = new Timer();
    TextEffectTest testEffect = new TextEffectTest();
    Polygon polygon = new Polygon(6, 40);
    Rectangle rectangle = new Rectangle(600, 150, 40, 40);
    Icon icon;
    Color rectColor = Color.BLACK;
    String foo = "This is a call to all my past resignations";
    
    float angle;
    
    int index;
    boolean reverse;
    
    public WidgetTest() {
        icon = new Icon(new Texture("xjge_texture_missing.png"), 64, 64, true);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        angle = (angle >= 360) ? 0 : angle + 1;
        
        icon.position.x = 400;
        icon.position.y = 40;
        icon.rotation.x = angle;
        icon.scale.x = 2;
        
        polygon.fill = true;
        polygon.position.set(200);
        polygon.rotation.z = angle;
        polygon.setColor(Color.ORANGE);
        
        if(XJGE.tick(4)) {
            if(reverse) {
                index--;
                if(index <= 0) reverse = false;
            } else {
                index++;
                if(index >= foo.length()) reverse = true;
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        polygon.render();
        font.drawString("The quick brown fox jumps\nover the lazy dog.", 50, 100, testEffect);
        font.drawString(foo.subSequence(0, index), 300, 200, null);
        rectangle.render(0.5f, rectColor);
        icon.render();
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
        //TODO: test other mouse input values
        
        rectColor = (mouse.hovered(rectangle)) ? Color.RED : Color.BLACK;
        
        if(mouse.clickedOnce()) {
            System.out.println("clicked");
        }
    }

    @Override
    public void delete() {
    }

}