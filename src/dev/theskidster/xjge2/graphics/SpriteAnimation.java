package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Game;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

public class SpriteAnimation {

    public int currFrame;
    public int speed;
    
    public List<Vector2i> frames = new ArrayList<>();
    
    public SpriteAnimation(Vector2i frame) {
        speed = 1;
        frames.add(frame);
    }
    
    public SpriteAnimation(int speed, List<Vector2i> frames) {
        this.speed  = speed;
        this.frames = frames;
    }
    
    public void updateAnimation(Atlas atlas) {
        if(currFrame < frames.size()) {
            if(Game.tick(speed)) {
                atlas.texCoords.set(
                        atlas.subImageOffsets.get(frames.get(currFrame)).x,
                        atlas.subImageOffsets.get(frames.get(currFrame)).y);
                currFrame++;
            }
        } else {
            currFrame = 0;
        }
    }
    
}