package org.xjge.graphics;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class SkeletalAnimation {

    public final float duration;
    public float ticksPerSecond;
    
    public final String name;
    
    public List<Keyframe> keyframes = new ArrayList<>();
    
    SkeletalAnimation(AIAnimation aiAnimation, Skeleton skeleton) {
        var aiChannels  = aiAnimation.mChannels();
        
        duration       = (float) aiAnimation.mDuration();
        ticksPerSecond = (float) (aiAnimation.mTicksPerSecond());
        var tempName   = aiAnimation.mName().dataString();

        int pipe = tempName.indexOf("|");
        name = (pipe != -1) ? tempName.substring(pipe + 1) : tempName;

        if(ticksPerSecond == 0f) ticksPerSecond = 25f;
        
        //Extract keyframes
        for(int c = 0; c < aiAnimation.mNumChannels(); c++) {
            var aiChannel = AINodeAnim.create(aiChannels.get(c));
            var boneName  = aiChannel.mNodeName().dataString();
            var boneIndex = skeleton.boneMap.get(boneName);

            if(boneIndex == null) continue;

            keyframes.add(new Keyframe(aiChannel, boneIndex));
        }
    }
    
}