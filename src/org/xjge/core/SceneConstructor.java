package org.xjge.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

//Apr 1, 2022

/**
 * @author J Hoffman
 * @since  2.1.2
 */
class SceneConstructor {

    Constructor con;
        Parameter[] params;
        
    SceneConstructor(Constructor con, Parameter[] params) {
        this.con    = con;
        this.params = params;
    }
}