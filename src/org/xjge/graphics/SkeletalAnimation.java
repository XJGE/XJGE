package org.xjge.graphics;

/**
 * Contains animation playback state
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class SkeletalAnimation {

    boolean playing     = true;
    boolean looping     = true;
    boolean wasFinished = false;

    double time = 0.0;

    SkeletalAnimationData animationData;

    SkeletalAnimation(SkeletalAnimationData animationData) {
        this.animationData = animationData;
    }

    void update(double deltaTime, double speed) {
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

    BoneTrack getBoneTrack(int boneIndex) {
        return animationData.boneTracks[boneIndex];
    }
    
}