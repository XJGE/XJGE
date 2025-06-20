package org.xjge.test;

import java.util.Map;
import org.xjge.core.Mouse;
import org.xjge.ui.Font;
import org.xjge.core.SplitScreenType;
import org.xjge.core.StopWatch;
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
public class TestWidget extends Widget {

    Font font = new Font("font_source_code_pro.ttf", 32);
    StopWatch timer = new StopWatch();
    TextEffectTest testEffect = new TextEffectTest();
    Polygon polygon = new Polygon(6, 40, 200, 200, Color.ORANGE, true);
    Rectangle rectangle = new Rectangle(600, 150, 40, 40);
    Icon icon;
    Color rectColor = Color.BLACK;
    
    float angle;
    
    int index;
    boolean reverse;
    
    public TestWidget() {
        icon = new Icon(new Texture("xjge_texture_missing.png"), 64, 64, true);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        icon.position.x = 400;
        icon.position.y = 40;
        
        angle = (angle >= 360) ? 0 : angle + 1; 
        
        icon.rotation.x = angle;
        
        icon.scale.x = 2;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        polygon.render(0.5f);
        font.drawString("The quick brown fox jumps\nover the lazy dog.", 50, 100, testEffect);
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
            //System.out.println("clicked");
        }
    }

    @Override
    public void delete() {
    }

}