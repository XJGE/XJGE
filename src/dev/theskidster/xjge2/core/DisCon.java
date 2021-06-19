package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.RectangleBatch;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: Jun 18, 2021
 */

final class DisCon extends Widget {

    private final RectangleBatch rectBatch;
    private final String message;
    
    private final Vector2i[] textPos = new Vector2i[2];
    
    public DisCon() {
        super(new Vector3i(), 100, 30);
        
        setSplitPosition();
        
        rectBatch  = new RectangleBatch(2);
        textPos[0] = new Vector2i();
        textPos[1] = new Vector2i();
        message    = Text.wrap("Please reconnect your controller.", width, engineFont);
    }

    @Override
    public void update() {}

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        rectBatch.batchStart(1);
            rectBatch.drawRectangle(position.x, position.y, width, 36, Color.BLACK);
            rectBatch.drawRectangle(position.x, position.y + 40, width, 16, Color.BLACK);
        rectBatch.batchEnd();
        
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