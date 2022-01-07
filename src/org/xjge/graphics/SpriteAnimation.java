package org.xjge.graphics;

import org.xjge.core.Game;
import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector2i;

//Created: Jun 13, 2021

/**
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
    
    public ArrayList<Vector2i> frames = new ArrayList<>();
    
    /**
     * Creates a new single-frame animation- a non-animation if you will.
     * 
     * @param frame the frame to display indefinitely until the animation is 
     *              changed
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
    public SpriteAnimation(int speed, ArrayList<Vector2i> frames) {
        this.speed  = speed;
        this.frames = frames;
    }
    
    /**
     * Steps the animation forward changing the texture coordinates of the 
     * sprite sheet and subsequently the sub-image rendered.
     * 
     * @param atlas the texture atlas (or sprite sheet) to use for this animation
     */
    public void update(Atlas atlas) {
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
    
    /**
     * Overloaded variant of the {@link update update()} method which stores 
     * the texture coordinates in the provided vector object. This is useful 
     * anytime you want to reuse the data from a texture atlas without 
     * altering its own {@code texCoords} field.
     * 
     * @param texCoords the vector object that can be passed to a shader 
     *                   program
     * @param atlas     the texture atlas (or sprite sheet) to use for this 
     *                   animation
     */
    public void update(Vector2f texCoords, Atlas atlas) {
        if(currFrame < frames.size()) {
            if(Game.tick(speed)) {
                texCoords.set(
                        atlas.subImageOffsets.get(frames.get(currFrame)).x,
                        atlas.subImageOffsets.get(frames.get(currFrame)).y);
                currFrame++;
            }
        } else {
            currFrame = 0;
        }
    }
    
}