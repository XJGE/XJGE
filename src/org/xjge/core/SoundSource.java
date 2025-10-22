package org.xjge.core;

import java.util.LinkedList;
import java.util.Queue;
import org.joml.Vector3f;
import static org.lwjgl.openal.AL11.*;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class SoundSource {
    
    private boolean playRequested;
    boolean reserved;
    boolean pausedInBulk;
    
    private float pitch  = 1f;
    private float volume = 1f;
    
    private int handle;
    private int state = AL_INITIAL;
    private int sampleOffset;
    public final int index;
    
    private final Vector3f tempPos = new Vector3f();
    public final Vector3f position = new Vector3f();
    
    private QueuedSound currentSound;
    
    private final Queue<QueuedSound> soundQueue = new LinkedList<>();
    
    private record QueuedSound(String name, boolean looping) {}
    
    SoundSource(int index) {
        this.index = index;
    }
    
    void cacheState() {
        pitch  = getPitch();
        volume = getVolume();
        state  = getState();
        
        sampleOffset = alGetSourcei(handle, AL_SAMPLE_OFFSET);
        
        stop();
        delete();
    }
    
    void applyState() {
        handle = alGenSources();
        
        setPitch(pitch);
        setVolume(volume);
        
        if(currentSound != null) {
            Sound sound = AudioSystem.getSound(currentSound.name);
            
            alSourceQueueBuffers(handle, sound.handle);
            alSourcei(handle, AL_LOOPING, (currentSound.looping) ? AL_TRUE : AL_FALSE);
            alSourcei(handle, AL_SAMPLE_OFFSET, sampleOffset);
        }
        
        for(QueuedSound queuedSound : soundQueue) {
            Sound sound = AudioSystem.getSound(queuedSound.name);
            alSourceQueueBuffers(handle, sound.handle);
        }
        
        switch(state) {
            case AL_PLAYING -> {
                playRequested = true;
                alSourcePlay(handle);
            }
            case AL_PAUSED  -> pause();
            case AL_STOPPED -> stop();
        }
    }
    
    void updatePosition(Vector3f cameraPosition, Vector3f cameraDirection) {
        position.sub(cameraPosition, tempPos);

        float dot   = tempPos.dot(cameraDirection);
        float det   = tempPos.x * cameraDirection.z - tempPos.z * cameraDirection.x; //determinant
        float angle = (float) Math.toDegrees(Math.atan2(det, dot)) - 90;

        if(angle < 0) {
            float offset = 180 + angle;
            angle = 180 + offset;
        }

        float dist = cameraPosition.distance(position);

        float rad = (float) Math.toRadians(angle);
        float x   = (float) -(dist * Math.cos(rad));
        float z   = (float) (dist * Math.sin(rad));
        
        alSource3f(handle, AL_POSITION, x, tempPos.y, z);
    }
    
    void updateQueue() {
        while(alGetSourcei(handle, AL_BUFFERS_PROCESSED) > 0) {
            alSourceUnqueueBuffers(handle);
        }
        
        if(!playRequested) return;
        
        if(alGetSourcei(handle, AL_BUFFERS_QUEUED) == 0 && !soundQueue.isEmpty()) {
            //Unbinding the previous buffers is required to make sources mono/stereo agnostic.
            alSourceStop(handle);
            while(alGetSourcei(handle, AL_BUFFERS_PROCESSED) > 0) {
                alSourceUnqueueBuffers(handle);
            }
            alSourcei(handle, AL_BUFFER, 0);
            
            currentSound = soundQueue.poll();
            Sound sound = AudioSystem.getSound(currentSound.name);
            
            alSourceQueueBuffers(handle, sound.handle);
            alSourcei(handle, AL_LOOPING, (currentSound.looping) ? AL_TRUE : AL_FALSE);
            
            if(getState() != AL_PLAYING) alSourcePlay(handle);
        }
    }
    
    void delete() {
        alDeleteSources(handle);
    }
    
    public void release() {
        reserved = false;
        Logger.logInfo("The reserved sound source at index " + index + 
                       " has been released by the user");
    }
    
    public boolean isLooping() {
        return currentSound != null && currentSound.looping;
    }
    
    public float getPitch() {
        return alGetSourcef(handle, AL_PITCH);
    }
    
    public float getVolume() {
        return alGetSourcef(handle, AL_GAIN);
    }
    
    public int getState() {
        return alGetSourcei(handle, AL_SOURCE_STATE);
    }
    
    public final String getCurrentSound() {
        return (currentSound != null) ? currentSound.name : null;
    }
    
    public SoundSource setLooping(boolean looping) {
        if(currentSound != null) {
            currentSound = new QueuedSound(currentSound.name, looping);
            
            if(looping && !soundQueue.contains(currentSound)) {
                soundQueue.add(currentSound);
            } else if(!looping) {
                soundQueue.remove(currentSound);
            }
        }
        
        alSourcei(handle, AL_LOOPING, (looping) ? AL_TRUE : AL_FALSE);
        
        return this;
    }
    
    public SoundSource setPitch(float pitch) {
        alSourcef(handle, AL_PITCH, pitch);
        return this;
    }
    
    public SoundSource setVolume(float volume) {
        alSourcef(handle, AL_GAIN, XJGE.clampValue(0f, 1f, volume));
        return this;
    }
    
    public SoundSource setPosition(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }
    
    public SoundSource seek(float seconds) {
        if(currentSound == null) return this;
        
        Sound sound = AudioSystem.getSound(currentSound.name);
        alSourcei(handle, AL_SAMPLE_OFFSET, Math.round(seconds * sound.frequency));
        
        return this;
    }
    
    public SoundSource queueSound(String name, boolean looping) {
        soundQueue.add(new QueuedSound(name, looping));
        return this;
    }
    
    public SoundSource play() {
        playRequested = true;
        if(getState() != AL_PLAYING && !soundQueue.isEmpty()) updateQueue();
        return this;
    }
    
    public SoundSource pause() {
        playRequested = false;
        alSourcePause(handle);
        return this;
    }
    
    public SoundSource stop() {
        playRequested = false;
        alSourceStop(handle);
        return this;
    }
    
}