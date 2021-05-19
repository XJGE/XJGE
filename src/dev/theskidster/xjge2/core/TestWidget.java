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
        
        String file1 = "fnt_source_code_pro_regular.ttf";
        String file2 = "fnt_inconsolata_regular.ttf";
        String file3 = "fnt_black_metal.ttf";
        String file4 = "fnt_goblin_one_regular.ttf";
        String file5 = "fnt_open_sans_regular.ttf";
        String file6 = "fnt_pattaya_regular.ttf";
        String file7 = "fnt_share_tech_regular.ttf";
        
        Font font = new Font(file5, 32);
        
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