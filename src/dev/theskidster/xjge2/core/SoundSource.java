package dev.theskidster.xjge2.core;

import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.alGenSources;

/**
 * @author J Hoffman
 * Created: Jun 23, 2021
 */

final class SoundSource {
    
    final int handle;
    
    private boolean loop;
    
    private Vector3f position;
    private Vector3f tempPos = new Vector3f();
    
    SoundSource() {
        handle = alGenSources();
    }
    
    SoundSource(SoundSource soundSource, int soundHandle, int sourceSample, int sourceState) {
        handle = alGenSources();
    }
    
}