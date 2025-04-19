package org.xjge.graphics;

/**
 * Created: Sep 7, 2021
 * <br><br>
 * Supplies a {@link Model} object with user-defined OpenGL capabilities.
 * <p>
 * By default model objects do not exhibit depth testing or face culling. This
 * functionality was left undefined so users could instead supplant whichever 
 * capabilities are required to render the model properly. These capabilities 
 * are essentially encapsulated in the GLCaps object and supplied to the 
 * models render method at runtime.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class GLCaps {

    /**
     * Creates a new GLCapabilites object which can be passed to the 
     * {@link Model#render render()} method of a {@link Model} object.
     */
    public GLCaps() {}
    
    /**
     * Processed before the models meshes are rendered. Calls to methods like 
     * {@code glEnable()} should be made here.
     */
    public abstract void enable();
    
    /**
     * Processed after the models meshes haver been rendered. Calls to methods 
     * like {@code glDisable()} should be made here to reset the state of 
     * whichever capabilities where enabled to render the model.
     */
    public abstract void disable();
    
}