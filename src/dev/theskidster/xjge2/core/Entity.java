package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

/**
 * Abstract class which can be used to represent dynamic game objects in the {@link Scene}.
 */
public abstract class Entity {

    private boolean remove;
    
    public Vector3f position;
    
    /**
     * Constructs a new Entity object. Most subclasses will likely overload this with their own arguments.
     * 
     * @param position the initial position of this entity in 3D space
     */
    protected Entity(Vector3f position) {
        this.position = position;
    }
    
    /**
     * Used to organize entity game logic.
     * 
     * @param targetDelta a constant value describing the desired delta time it should take for one game tick to complete
     */
    public abstract void update(double targetDelta);
    
    /**
     * Legacy render method included in the event an entity only wishes to use a single {@link GLProgram} during rendering.
     * 
     * @param glProgram the shader program that will be used to render this entity
     * @param camera    the {@link Camera} object of the current {@link Viewport} being rendered
     * @param lights    an array of light source objects provided by the current {@link Scene}
     * @param numLights the total number of lights the scene is actively using
     * 
     * @see render(Map, Camera, LightSource[], int)
     */
    public abstract void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights);
    
    /**
     * Used to organize the OpenGL draw calls required to render this entity in the game world.
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     * @param camera     the {@link Camera} object of the current {@link Viewport} being rendered
     * @param lights     an array of light source objects provided by the current {@link Scene}
     * @param numLights  the total number of lights the scene is actively using
     * 
     * @see render(GLProgram, Camera, LightSource[], int)
     */
    public abstract void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights);
    
    
    /**
     * Used to free the resources used by the entity once it is no longer needed, Calls like 
     * {@link dev.theskidster.xjge2.graphics.Graphics#freeBuffers Graphics.freeBuffers()} and 
     * {@link dev.theskidster.xjge2.graphics.Texture#freeTexture Texture.freeTexture()} should be made here.
     * <br><br>
     * NOTE: This method should <i>only</i> be used to deallocate memory. Death animations and other effects should be included in the 
     * entities game logic via the {@link update update()} method.
     */
    protected abstract void destroy();
    
    /**
     * Requests the removal and destruction of this entity.
     */
    protected void remove() {
        remove = true;
    }
    
    /**
     * Finds if the entity has made a request for {@linkplain remove removal}. If it has, the entity will be {@linkplain destroy destroyed} and 
     * subsequently removed from the current scenes {@linkplain Scene#entities entities collection}.
     * 
     * @return true if the entity has requested removal
     */
    public boolean removalRequested() {
        if(remove) destroy();
        return remove;
    }
    
}