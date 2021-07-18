package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Game;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

/**
 * Represents a 2D sprite animation that iterates over a series of sub-images from a {@link SpriteSheet} in sequence to create the illusion 
 * of movement.
 */
public class SpriteAnimation {

    public int currFrame;
    public int speed;
    
    public List<Vector2i> frames = new ArrayList<>();
    
    /**
     * Creates a new single-frame animation- a non-animation if you will.
     * 
     * @param frame the frame to display indefinitely until the animation is changed
     */
    public SpriteAnimation(Vector2i frame) {
        speed = 1;
        frames.add(frame);
    }
    
    /**
     * Creates a new 2D animation using the frames provided.
     * 
     * @param frames the frames that comprise the animation in sequence
     * @param speed  the speed of the animation in game ticks
     */
    public SpriteAnimation(int speed, List<Vector2i> frames) {
        this.speed  = speed;
        this.frames = frames;
    }
    
    /**
     * Steps the animation forward changing the texture coordinates of the {@link SpriteSheet} and subsequently the sub-image rendered.
     * 
     * @param atlas the texture atlas (or sprite sheet) to use for this animation
     */
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