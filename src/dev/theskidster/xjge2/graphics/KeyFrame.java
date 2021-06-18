package dev.theskidster.xjge2.graphics;

import java.util.ArrayList;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class KeyFrame {

    final ArrayList<Matrix4f> transforms = new ArrayList<>();
    
    KeyFrame() {
        for(int b = 0; b < Model.MAX_BONES; b++) transforms.add(new Matrix4f());
    }
    
}