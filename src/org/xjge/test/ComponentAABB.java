package org.xjge.test;

import org.xjge.core.Component;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentAABB extends Component {

    float width;
    float height;
    
    ComponentAABB(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    void render(GLProgram shader) {
        
    }
    
}