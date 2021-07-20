package dev.theskidster.xjge2.core;

import org.joml.Vector3f;
import static org.lwjgl.openal.AL11.*;

//Created: Jun 23, 2021

/**
 * Represents an OpenAL source object.
 * <p>
 * These objects can be best conceptualized as invisible CD players/speakers 
 * located throughout the game world, with the CD itself acting as the sources
 * current {@link Sound} or {@link Song} to play.
 * </p>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class SoundSource {
    
    final int handle;
    
    private boolean loop;
    
    private Vector3f position;
    private Vector3f tempPos = new Vector3f();
    
    /**
     * Generates a new empty sound source with no other information.
     */
    SoundSource() {
        handle = alGenSources();
    }
    
    /**
     * Creates a new sound source from an existing one.
     * 
     * @param source       the sound source to copy
     * @param sound        the sound to load this source with
     * @param sourceSample the current sample of the source used to offset the 
     *                     point from which the sound will continue playing
     * @param sourceState  the state to set the source object to
     */
    SoundSource(SoundSource source, Sound sound, int sourceSample, int sourceState) {
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
    
    /**
     * Creates a new sound source from an existing one.
     * <p>
     * Variant of {@link SoundSource(SoundSource, Sound, int, int)} for copying 
     * a sound source that was playing music.
     * </p>
     * 
     * @param source        the sound source to copy
     * @param sound         the sound to load this source with
     * @param sourceSample  the current sample of the source used to offset the 
     *                      point from which the sound will continue playing
     * @param sourceState   the state to set the source object to
     * @param introFinished if true, the intro sequence of the current song has 
     *                      finished and the body may now be looped
     */
    SoundSource(SoundSource source, Song song, int sourceSample, int sourceState, boolean introFinished) {
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
    
    /**
     * Obtains the current state of the sound source.
     * 
     * @param state the state we want to check for. One of: 
     * <table><tr>
     * <td>{@link org.lwjgl.openal.AL11#AL_INITIAL AL_INITIAL}</td>
     * <td>{@link org.lwjgl.openal.AL11#AL_PAUSED AL_PLAYING}</td>
     * <td>{@link org.lwjgl.openal.AL11#AL_PAUSED AL_PAUSED}</td>
     * <td>{@link org.lwjgl.openal.AL11#AL_PAUSED AL_STOPPED}</td>
     * </tr></table>
     * 
     * @return the current state of the source object 
     */
    boolean getState(int state) {
        return alGetSourcei(handle, AL_SOURCE_STATE) == state;
    }
    
    /**
     * Obtains the sound sources current position within the game world.
     * 
     * @return the position the sound source is emitting noise from
     */
    Vector3f getPosition() {
        return position;
    }
    
    /**
     * Sets the position of the sound source object as its found in the game 
     * world. This vector will be used in conjunction with the orientation of 
     * the nearest {@link Camera} to calculate its final position relative to 
     * the OpenAL listener object.
     * 
     * @param position the position to place the sound source at
     */
    void setWorldPosition(Vector3f position) {
        if(position != null) this.position = position;
    }
    
    /**
     * Calculates the final position of the sound source given its location in 
     * the game world relative to the orientation of the nearest viewports 
     * {@link Camera} object.
     * 
     * @param position  the current position of the viewports camera
     * @param direction the direction the viewports camera is currently facing
     */
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
    
    /**
     * Sets this sound source to start or stop playing in a loop. Loops will 
     * continue indefinitely until specified otherwise or the sound source 
     * enters a {@link org.lwjgl.openal.AL11#AL_PAUSED AL_STOPPED} state.
     * 
     * @param loop if true, the sound used by this source object will play in a 
     *             loop
     */
    void setLooping(boolean loop) {
        this.loop = loop;
        alSourcei(handle, AL_LOOPING, (loop) ? AL_TRUE : AL_FALSE);
    }
    
    /**
     * Sets the current {@link Sound} that the sound source will use. 
     * <p>
     * NOTE: Calling this method on a newly initialized source object will set 
     * its source type from 
     * {@link org.lwjgl.openal.AL11#AL_UNDETERMINED AL_UNDETERMINED} to 
     * {@link org.lwjgl.openal.AL11#AL_STATIC AL_STATIC}. Which will prevent it 
     * from allowing additional sounds to be queued through 
     * {@link queueSound(Sound) queueSound()} and cause an 
     * {@linkplain dev.theskidster.xjge2.core.ErrorUtils#checkALError() AL Error}.
     * </p>
     * 
     * @param sound the sound to bind to this source
     */
    void setSound(Sound sound) {
        alSourcei(handle, AL_BUFFER, sound.handle);
    }
    
    /**
     * Queues a sound to play on this sound source after prior sounds in the 
     * queue have finished. 
     * <p>
     * NOTE: This method will cause an 
     * {@linkplain dev.theskidster.xjge2.core.ErrorUtils#checkALError() AL Error} 
     * if a source type is not 
     * {@link org.lwjgl.openal.AL11#AL_STREAMING AL_STREAMING}. OpenAL requires 
     * a source to make an initial call to 
     * {@link org.lwjgl.openal.AL11#alSourceQueueBuffers(int, int) alSourceQueueBuffers()} 
     * prior to other operations to set its type, as its type cannot be set 
     * explicitly later. The reserved music source object by default uses this 
     * type.
     * </p>
     * 
     * @param sound the sound to queue
     */
    void queueSound(Sound sound) {
        alSourceQueueBuffers(handle, sound.handle);
    }
    
}