package dev.theskidster.xjge2.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class KeyFrame {

    private List<Matrix4f> transforms;
    
    KeyFrame() {
        transforms = new ArrayList<>();
        for(int b = 0; b < Model.MAX_BONES; b++) transforms.add(new Matrix4f());
        transforms = Collections.unmodifiableList(transforms);
    }
    
    Matrix4f getTransform(int index) {
        return transforms.get(index);
    }
    
    List<Matrix4f> getTransformData() {
        return transforms;
    }
    
}