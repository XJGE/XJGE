package dev.theskidster.xjge2.core;

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
 * Created: May 18, 2021
 */

class TestWidget extends Widget {

    private String wrapped;
    
    private Text text;
    private Vector3f textPos1 = new Vector3f(0, 300, 0);
    private Vector3f textPos2 = new Vector3f(0, 252, 0);
    private Vector3f textPos3 = new Vector3f(0, 204, 0);
    private Vector3f textPos4 = new Vector3f(0, 156, 0);
    private Vector3f textPos5 = new Vector3f(0, 108, 0);
    private Vector3f textPos6 = new Vector3f(0, 60, 0);
    
    public TestWidget() {
        super(new Vector3i(), 300, 200);
        
        Font font = new Font("fnt_debug_mono.ttf", 32);
        
        text = new Text(font);
        wrapped = Text.wrap("The quick brown fox jumps over the lazy dog.", 200, font);
    }

    @Override
    public Command update() {
        return null;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        text.drawString(wrapped, textPos1, Color.BLACK);
        
        /*
        text.drawString(" !\"#$%&\'()*+,-./", textPos1, Color.RED);
        text.drawString("0123456789:;<=>?", textPos2, Color.BLACK);
        text.drawString("@ABCDEFGHIJKLMNO", textPos3, Color.BLACK);
        text.drawString("PQRSTUVWXYZ[\\]^_", textPos4, Color.BLACK);
        text.drawString("`abcdefghijklmno", textPos5, Color.BLACK);
        text.drawString("pqrstuvwxyz{|}~", textPos6, Color.BLACK);
        */
    }

    @Override
    public void setSplitPosition() {
    }

    //TODO: this is temp class for testing purposes only and should not be
    //included in future releases.
    
}