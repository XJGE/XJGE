package org.xjge.graphics;

import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.xjge.core.Timer;
import org.xjge.core.XJGE;

/**
 * Created: Jun 13, 2021
 * <br><br>
 * Represents a 2D sprite animation that iterates over a series of sub-images 
 * from a {@linkplain Atlas texture atlas} in sequence to create the illusion 
 * of movement.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class SpriteAnimation {

    public int currFrame;
    public int speed;
    
    private final int initialSpeed;
    
    private final Timer stopWatch = new Timer();
    public ArrayList<Vector2i> frames = new ArrayList<>();
    
    /**
     * Creates a new single-frame animation- a non-animation if you will.
     * 
     * @param frame the frame to display indefinitely until the animation is changed
     */
    public SpriteAnimation(Vector2i frame) {
        frames.add(frame);
        speed        = 1;
        initialSpeed = speed;
    }
    
    /**
     * Creates a new 2D animation using the frames provided.
     * 
     * @param frames the frames that comprise the animation in sequence
     * @param speed the speed of the animation in game ticks
     */
    public SpriteAnimation(ArrayList<Vector2i> frames, int speed) {
        this.frames  = frames;
        this.speed   = speed;
        initialSpeed = speed;
    }
    
    /**
     * Steps the animation forward changing the texture coordinates of the 
     * sprite sheet and subsequently the sub-image rendered.
     * 
     * @param atlas the texture atlas (or sprite sheet) to use for this animation
     * @param sync if true, the animation will use a global timing mechanism. 
     *             This is useful for synchronizing animations between objects.
     */
    public void update(Atlas atlas, boolean sync) {
        if(speed == 0) return;
        
        if(sync) {
            if(XJGE.tick(speed)) {
                currFrame++;
                if(currFrame >= frames.size()) currFrame = 0;
            }
        } else {
            if(stopWatch.tick(frames.size(), speed, true)) {
                currFrame = stopWatch.getTime();
            }
        }
        
        atlas.texCoords.set(atlas.subImageOffsets.get(frames.get(currFrame)).x,
                            atlas.subImageOffsets.get(frames.get(currFrame)).y);
    }
    
    /**
     * Overloaded variant of the {@link update update()} method which stores 
     * the texture coordinates in the provided vector object. This is useful 
     * anytime you want to reuse the data from a texture atlas without 
     * altering its own {@code texCoords} field.
     * 
     * @param texCoords the vector object that can be passed to a shader 
     *                  program
     * @param atlas the texture atlas (or sprite sheet) to use for this 
     *              animation
     * @param sync if true, the animation will use a global timing mechanism. 
     *             This is useful for synchronizing animations between objects.
     */
    public void update(Vector2f texCoords, Atlas atlas, boolean sync) {
        if(speed == 0) return;
        
        if(sync) {
            if(XJGE.tick(speed)) {
                currFrame++;
                if(currFrame >= frames.size()) currFrame = 0;
            }
        } else {
            if(stopWatch.tick(frames.size(), speed, true)) {
                currFrame = stopWatch.getTime();
            }
        }
        
        texCoords.set(atlas.subImageOffsets.get(frames.get(currFrame)).x,
                      atlas.subImageOffsets.get(frames.get(currFrame)).y);
    }
    
    /**
     * Restores the sprite animation to its initial settings including changing
     * the current frame to the first in sequence, and reverting the speed to
     * that which was specified through its constructor.
     * <p>
     * Often this is useful in instances where an animation was stopped partway 
     * through and you don't want to resume it from the previous frame it was 
     * on before it stopped.
     */
    public void reset() {
        currFrame = 0;
        speed     = initialSpeed;
        stopWatch.reset();
    }
    
}