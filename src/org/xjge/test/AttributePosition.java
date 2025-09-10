package org.xjge.test;

import org.joml.Vector3f;
import org.xjge.core.Attribute;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class AttributePosition extends Attribute {

    final Vector3f position;
    
    AttributePosition(float x, float y, float z) {
        position = new Vector3f(x, y, z);
    }
    
}