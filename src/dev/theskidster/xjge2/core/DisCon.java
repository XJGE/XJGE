package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import java.util.Map;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: Jun 18, 2021
 */

final class DisCon extends Widget {

    private final RectangleBatch rectBatch;
    
    
    public DisCon(int width, int height) {
        super(new Vector3i(), width, height);
        
        setSplitPosition();
        
        rectBatch = new RectangleBatch(2);
        
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
    }

    @Override
    public void setSplitPosition() {
        switch(XJGE.getScreenSplit()) {
            case NONE -> {
                
            }
                
            case HORIZONTAL -> {
                
            }
                
            case VERTICAL -> {
                
            }
                
            case TRISECT -> {
                
            }
                
            case QUARTER -> {
                
            }
        }
    }
    
}