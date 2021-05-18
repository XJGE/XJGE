package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import dev.theskidster.xjge2.ui.Font;
import dev.theskidster.xjge2.ui.Text;
import dev.theskidster.xjge2.ui.Widget;
import java.util.Map;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: May 18, 2021
 */

class TestWidget extends Widget {

    private Text text;
    private Vector3i textPos = new Vector3i(0, 0, 0);
    
    public TestWidget() {
        super(new Vector3i(), 300, 200);
        
        String file1 = "fnt_source_code_pro_regular.ttf";
        String file2 = "fnt_inconsolata_regular.ttf";
        String file3 = "fnt_black_metal.ttf";
        
        text = new Text(new Font(file3, 48));
    }

    @Override
    public Command update() {
        return null;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        text.draw(glPrograms.get("default"), "weedle!69", textPos, Color.BLACK);
    }

    @Override
    public void setSplitPosition() {
    }

    //TODO: this is temp class for testing purposes only and should not be
    //included in future releases.
    
}