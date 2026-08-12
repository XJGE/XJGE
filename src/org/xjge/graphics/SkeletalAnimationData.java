package org.xjge.graphics;

import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;

/**
 * Immutable animation data pulled directly from the model asset file
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class SkeletalAnimationData {

    public final float duration;
    public final float ticksPerSecond;
    
    public final String name;
    
    public final BoneTrack[] boneTracks;
    
    SkeletalAnimationData(AIAnimation aiAnimation, Skeleton skeleton) {
        var aiChannels  = aiAnimation.mChannels();
        float tempTPS   = (float) (aiAnimation.mTicksPerSecond());
        var tempName    = aiAnimation.mName().dataString();
        int pipeIndex   = tempName.indexOf("|");
        
        duration       = (float) aiAnimation.mDuration();
        ticksPerSecond = (tempTPS == 0f) ? 25f : tempTPS;
        name           = (pipeIndex != -1) ? tempName.substring(pipeIndex + 1) : tempName;
        boneTracks     = new BoneTrack[skeleton.getBoneCount()];
        
        //Extract keyframe data from every bone and store it as a new track
        for(int c = 0; c < aiAnimation.mNumChannels(); c++) {
            var aiChannel = AINodeAnim.create(aiChannels.get(c));
            var boneName  = aiChannel.mNodeName().dataString();
            
            if(!skeleton.hasBone(boneName)) continue;
            
            var boneIndex = skeleton.getBoneIndex(boneName);
            boneTracks[boneIndex] = new BoneTrack(aiChannel);
        }
    }
    
}