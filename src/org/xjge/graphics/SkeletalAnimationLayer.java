package org.xjge.graphics;

import org.xjge.core.XJGE;

/**
 * Represents a single animation layer that can be used to change animation playback settings during runtime
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class SkeletalAnimationLayer {

    private float weight = 1f;
    
    private double speed = 1f;
    private double blendTime;
    private double blendDuration;

    private SkeletalAnimation current;
    private SkeletalAnimation next;
    
    private BoneMask mask;

    void update(double deltaTime) {
        if(current == null) return;

        current.update(deltaTime, speed);

        if(next != null) {
            next.update(deltaTime, speed);
            blendTime += deltaTime;

            if(blendTime >= blendDuration) {
                current = next;
                next    = null;
            }
        }
    }
    
    SkeletalAnimation getCurrent() {
        return current;
    }
    
    SkeletalAnimation getNext() {
        return next;
    }
    
    public void play(SkeletalAnimationData animationData) {
        current   = new SkeletalAnimation(animationData); //TODO: promotes gc churn? Maybe provide one that accepts initialized animation?
        next      = null;
        blendTime = 0;
    }

    public void crossfade(SkeletalAnimationData animationData, float duration) {
        if(current == null) {
            play(animationData);
            return;
        }

        next          = new SkeletalAnimation(animationData); //TODO: promotes gc churn? Maybe provide one that accepts initialized animation?
        blendTime     = 0;
        blendDuration = Math.max(duration, 0.0001);
    }
    
    public void resume() {
        if(current != null) current.playing = true;
    }
    
    public void pause() {
        if(current != null) current.playing = false;
    }
    
    public void stop() {
        current = null;
        next    = null;
    }
    
    public void setLooping(boolean looping) {
        if(current != null) current.looping = looping;
    }
    
    public void setWeight(float weight) {
        this.weight = XJGE.clampValue(0f, 1f, weight);
    }
    
    public void setNormalizedTime(float time) {
        if(current != null) {
            double durationSeconds = current.animationData.duration / current.animationData.ticksPerSecond;
            current.time = time * durationSeconds;
        }
    }
    
    public void setTime(double time) { //TODO: What good is this really? Normalized time is way better
        if(current != null) current.time = time;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void setMask(BoneMask mask) {
        this.mask = mask;
    }
    
    public boolean isPlaying() {
        return current != null && current.playing;
    }
    
    public boolean isLooping() {
        return (current != null) ? current.looping : false; 
    }
    
    public boolean isFinished() {
        if(current == null) return true;
        if(next != null) return false;
        return current.isFinished();
    }
    
    public boolean justFinished() { //Only fired once upon initial finish, similar to buttonPressedOnce()
        if(current == null) return false;
        if(next != null) return false;
        return current.justFinished();
    }
    
    public boolean isBlending() {
        return next != null;
    }
    
    public float getBlendFactor() {
        if(next == null) return 0f;
        return (float) Math.min(blendTime / blendDuration, 1.0);
    }
    
    public float getWeight() {
        return weight;
    }
    
    public double getTime() {
        if(current == null) return 0;
        if(next != null) return next.time;
        return current.time;
    }
    
    public double getNormalizedTime() {
        if(current == null) return 0f;

        double durationSeconds = current.animationData.duration / current.animationData.ticksPerSecond;
        if(durationSeconds == 0) return 0f;

        return current.time / durationSeconds; //TODO: clamp for saftey? Values can be negative (when speed is negative)
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public BoneMask getMask() {
        return mask;
    }
    
}