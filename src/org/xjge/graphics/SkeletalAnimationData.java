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
public final class SkeletalAnimationData {

    public final float duration;
    public final float ticksPerSecond;
    
    public final String name;
    
    public final List<Keyframe> keyframes = new ArrayList<>();
    
    SkeletalAnimationData(AIAnimation aiAnimation, Skeleton skeleton) {
        var aiChannels  = aiAnimation.mChannels();
        float aiTPS     = (float) (aiAnimation.mTicksPerSecond());
        
        duration       = (float) aiAnimation.mDuration();
        ticksPerSecond = (aiTPS == 0f) ? 25f : aiTPS;
        var tempName   = aiAnimation.mName().dataString();

        int pipe = tempName.indexOf("|");
        name = (pipe != -1) ? tempName.substring(pipe + 1) : tempName;
        
        //Extract keyframes
        for(int c = 0; c < aiAnimation.mNumChannels(); c++) {
            var aiChannel = AINodeAnim.create(aiChannels.get(c));
            var boneName  = aiChannel.mNodeName().dataString();
            
            if(!skeleton.hasBone(boneName)) continue;
            
            keyframes.add(new Keyframe(aiChannel, skeleton.getBoneIndex(boneName)));
        }
    }
    
}