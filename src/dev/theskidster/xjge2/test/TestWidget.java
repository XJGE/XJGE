package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.ui.Font;
import dev.theskidster.xjge2.ui.Font2;
import dev.theskidster.xjge2.ui.Text;
import dev.theskidster.xjge2.core.Widget;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
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
    
    private final Font font;
    
    private LinkedHashSet<String> words   = new LinkedHashSet<>();
    private TreeMap<Integer, Character> m = new TreeMap<>();
    
    public TestWidget() {
        super(new Vector3i(), 0, 0);
        
        text = new Text(new Font());
        font = new Font();
    }

    @Override
    public void update() {
        
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        /*
        
        uses two different glyph maps for each call- retains texutre info
        
        
        
        text.drawString("text 1", pos1, col1);
        text.drawString("text 2", pos2, col2);
        
        drawString as part of widget?
        
        
        Advantages of using drawString as part of widget;
        
        - can index each drawString call and reset it at the end.
        
        */
        
        drawString(font, "bleh", textPos, Color.BLACK);
        drawString(font, "bleh2", textPos, Color.BLACK);
        
        //text.drawString("bleh", textPos, Color.BLACK);
        //text.drawString("bleh2", textPos2, Color.BLACK);
        //font.draw(glyphs, false);
    }

    @Override
    public void setSplitPosition() {
        
    }

}