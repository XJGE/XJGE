package org.xjge.test;

import org.joml.Vector3f;
import org.xjge.core.Component;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentPosition extends Component {

    final Vector3f position;
    
    ComponentPosition(float x, float y, float z) {
        position = new Vector3f(x, y, z);
    }
    
}