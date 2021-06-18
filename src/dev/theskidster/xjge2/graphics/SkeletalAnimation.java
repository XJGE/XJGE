package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Game;
import java.util.List;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIAnimation;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class SkeletalAnimation {

    final float duration;
    private float frameTime;
    private float seekTime;
    
    final boolean transition;
    private boolean finished;
    
    final String name;
    final String nextAnim;
    private KeyFrame prevFrame;
    private final KeyFrame currFrame = new KeyFrame();
    private KeyFrame nextFrame;
    
    private final List<KeyFrame> frames;
    
    SkeletalAnimation(AIAnimation aiAnimation, List<KeyFrame> frames) {
        duration    = (float) aiAnimation.mDuration();
        name        = parseName(aiAnimation.mName().dataString());
        this.frames = frames;
        transition  = false;
        nextAnim    = null;
    }
    
    SkeletalAnimation(String currAnim, String nextAnim, List<KeyFrame> frames) {
        duration      = frames.size();
        name          = "Transition|" + currAnim + "->" + nextAnim;
        this.frames   = frames;
        transition    = true;
        this.nextAnim = nextAnim;
    }
    
    private String parseName(String aiName) {
        return (aiName.contains("Armature|")) ? aiName.substring(aiName.indexOf("|") + 1, aiName.length()) : aiName;
    }
    
    private void step(float speed, boolean loop) {
        if(speed > 0) {
            frameTime += (speed + Game.getDelta());
            seekTime  += (speed + Game.getDelta());
        }
        
        if(frameTime > 1) frameTime %= 1;
        
        if(transition) finished = (seekTime >= duration - 2);
        
        if(seekTime > duration) {
            if(loop) {
                seekTime %= duration;
            } else {
                frameTime = 0;
                seekTime  = duration;
            }
        }
    }
    
    private void findNearestFrames() {
        prevFrame = frames.get(0);
        nextFrame = frames.get(0);
        
        for(int f = 0; f < frames.size(); f++) {
            nextFrame = frames.get(f);
            
            if(f > seekTime) {
                if(f == frames.size() - 1) nextFrame = frames.get(0);
                else break;
            }
            
            prevFrame = frames.get(f);
        }
    }
    
    float getFrameTime() {
        return frameTime;
    }
    
    float getSeekTime() {
        return seekTime;
    }
    
    boolean getFinished() {
        return finished;
    }
    
    KeyFrame getCurrFrame() {
        return currFrame;
    }
    
    KeyFrame getFrame(int index) {
        return frames.get(index);
    }
    
    void setFrameTime(float frameTime) {
        if(frameTime > 1)      frameTime = 1;
        else if(frameTime < 0) frameTime = 0;
        
        this.frameTime = frameTime;
    }
    
    void setSeekTime(float seekTime) {
        this.seekTime = seekTime;
    }
    
    void genCurrFrame(float speed, boolean loop) {
        step(speed, loop);
        findNearestFrames();
        
        for(int b = 0; b < Model.MAX_BONES; b++) {
            prevFrame.transforms.get(b).lerp(nextFrame.transforms.get(b), frameTime, currFrame.transforms.get(b));
        }
    }
    
    Matrix4f calcTransition(int index) {
        findNearestFrames();
        Matrix4f result = new Matrix4f();
        prevFrame.transforms.get(index).lerp(nextFrame.transforms.get(index), frameTime, result);
        
        return result;
    }
    
}