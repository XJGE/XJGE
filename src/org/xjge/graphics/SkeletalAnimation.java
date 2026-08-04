package org.xjge.graphics;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class SkeletalAnimation {

    boolean playing     = true;
    boolean looping     = true;
    boolean wasFinished = false;

    double time  = 0.0;
    double speed = 1.0;

    SkeletalAnimationData animationData;

    Keyframe[] keyframesByBone;

    SkeletalAnimation(SkeletalAnimationData animationData, Model model) {
        this.animationData = animationData;
        keyframesByBone    = new Keyframe[model.getSkeleton().getBoneCount()];

        for(var keyframe : animationData.keyframes) keyframesByBone[keyframe.boneIndex] = keyframe;
    }

    void update(double deltaTime) {
        if(!playing) return;

        time += deltaTime * speed;

        double durationSeconds = animationData.duration / animationData.ticksPerSecond;

        if(looping) {
            time %= durationSeconds;
            if(time < 0.0) time += durationSeconds;
        } else {
            if(time < 0.0) time = 0.0;
            else if(time > durationSeconds) time = durationSeconds;
        }
    }

    boolean isFinished() {
        if(looping) return false;
        double durationSeconds = animationData.duration / animationData.ticksPerSecond;
        return time >= durationSeconds;
    }

    boolean justFinished() {
        boolean nowFinished = isFinished();
        boolean result      = nowFinished && !wasFinished;
        wasFinished         = nowFinished;
        return result;
    }

    float getAnimationTime() {
        double ticks = getTimeInTicks();

        if(!looping) {
            ticks = Math.max(0.0, Math.min(ticks, animationData.duration));
        }

        return (float) ticks;
    }

    double getTimeInTicks() {
        return time * animationData.ticksPerSecond;
    }

    Keyframe getKeyframe(int boneIndex) {
        return keyframesByBone[boneIndex];
    }
    
}