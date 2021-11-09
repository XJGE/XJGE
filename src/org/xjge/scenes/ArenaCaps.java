package org.xjge.scenes;

import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import org.xjge.graphics.GLCapabilities;

/**
 * Nov 9, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class ArenaCaps extends GLCapabilities {

    @Override
    public void enable() {
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void disable() {
        glDisable(GL_DEPTH_TEST);
    }

}
