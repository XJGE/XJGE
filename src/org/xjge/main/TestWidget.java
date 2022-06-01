package org.xjge.main;

import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.xjge.core.Split;
import org.xjge.core.Widget;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Point;
import org.xjge.graphics.Polygon;
import org.xjge.graphics.Rectangle;

/**
 *
 * @author thesk
 */
public class TestWidget extends Widget {

    private Point point;
    private Polygon poly;
    private Rectangle rect;
    private Vector2i textPos = new Vector2i(40, 80);
    
    public TestWidget() {
        super(new Vector3i(), 384, 216);
        
        point = new Point(40, 40, 0);
        poly  = new Polygon(5, true, 14, Color.ORANGE, 60, 20);
        rect  = new Rectangle(100, 40, 120, 80);
    }
    
    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        point.render(5);
        
        drawString(defaultFont, "test.", textPos, Color.WHITE);
        
        poly.render();
        rect.render(0.5f, Color.BLUE);
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
