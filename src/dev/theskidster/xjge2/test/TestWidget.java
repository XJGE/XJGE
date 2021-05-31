package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.ui.Font;
import dev.theskidster.xjge2.ui.Text;
import dev.theskidster.xjge2.ui.Widget;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 30, 2021
 */

public class TestWidget extends Widget {

    private final Text text;
    private final Vector3f textPos  = new Vector3f(40, 100, 0);
    private final Vector3f textPos2 = new Vector3f(40, 70, 0);
    
    public TestWidget() {
        super(new Vector3i(), 0, 0);
        
        text = new Text(new Font());
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        text.drawString("bleh", textPos, Color.BLACK);
        text.drawString("bleh2", textPos2, Color.BLACK);
    }

    @Override
    public void setSplitPosition() {
        
    }

}