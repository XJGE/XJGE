package dev.theskidster.xjge2.core;

import org.joml.Vector3f;
import static org.lwjgl.openal.AL11.*;

/**
 * @author J Hoffman
 * Created: Jun 23, 2021
 */

final class Source {
    
    final int handle;
    
    private boolean loop;
    
    private Vector3f position;
    private Vector3f tempPos = new Vector3f();
    
    //TODO: change to Source;
    Source() {
        handle = alGenSources();
    }
    
    Source(Source source, Sound sound, int sourceSample, int sourceState) {
        handle = alGenSources();
        
        
        setLooping(source.loop);
        setWorldPosition(source.position);
        
        if(sound != null) {
            setSound(sound);
            alSourcei(handle, AL_SAMPLE_OFFSET, sourceSample);
            
            switch(sourceState) {
                case AL_PLAYING -> alSourcePlay(handle);
                case AL_PAUSED  -> alSourcePause(handle);
                case AL_STOPPED -> alSourceStop(handle);
            }
        }
        
        ErrorUtils.checkALError();
    }
    
    Source(Source source, Song song, int sourceSample, int sourceState, boolean introFinished) {
        handle = alGenSources();
        
        if(song != null) {
            if(song.intro != null && !introFinished) {
                queueSound(song.intro);
                queueSound(song.body);
            } else {
                queueSound(song.body);
            }
            
            setLooping(source.loop);
            alSourcei(handle, AL_SAMPLE_OFFSET, sourceSample);
            
            switch(sourceState) {
                case AL_PLAYING -> alSourcePlay(handle);
                case AL_PAUSED -> alSourcePause(handle);
                case AL_STOPPED -> alSourceStop(handle);
            }
        }
        
        ErrorUtils.checkALError();
    }
    
    boolean getState(int state) {
        return alGetSourcei(handle, AL_SOURCE_STATE) == state;
    }
    
    Vector3f getPosition() {
        return position;
    }
    
    void setWorldPosition(Vector3f position) {
        if(position != null) this.position = position;
    }
    
    void setSourcePosition(Vector3f position, Vector3f direction) {
        if(this.position != null) {
            this.position.sub(position, tempPos);
            
            float dot   = tempPos.dot(direction);
            float det   = tempPos.x * direction.z - tempPos.z * direction.x; //determinant
            float angle = (float) Math.toDegrees(Math.atan2(det, dot)) - 90;
            
            if(angle < 0) {
                float offset = 180 + angle;
                angle = 180 + offset;
            }

            float dist = position.distance(this.position);

            float rad = (float) Math.toRadians(angle);
            float x   = (float) -(dist * Math.cos(rad));
            float z   = (float) (dist * Math.sin(rad));

            alSource3f(handle, AL_POSITION, x, tempPos.y, z);
        }
    }
    
    void setLooping(boolean loop) {
        this.loop = loop;
        alSourcei(handle, AL_LOOPING, (loop) ? AL_TRUE : AL_FALSE);
    }
    
    void setSound(Sound sound) {
        alSourcei(handle, AL_BUFFER, sound.handle);
    }
    
    void queueSound(Sound sound) {
        alSourceQueueBuffers(handle, sound.handle);
    }
    
}