package dev.theskidster.xjge2.graphics;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class Bone {

    int id;
    String name;
    Matrix4f offset;
    
    Bone(int id, String name, AIMatrix4x4 offset) {
        this.id     = id;
        this.name   = name;
        this.offset = Graphics.convertFromAssimp(offset);
    }
    
}