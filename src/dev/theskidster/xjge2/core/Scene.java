package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.Light;
import dev.theskidster.xjge2.graphics.Texture;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

/**
 * A 3D representation of the game world that contains entities, light sources, and camera objects.
 * 
 * @see Entity
 * @see LightSource
 * @see Camera
 */
public abstract class Scene {

    /**
     * The maximum number of light source objects that may be simultaneously present in the scene at any given time.
     */
    public static final int MAX_LIGHTS = 32;
    
    private int currLightIndex;
    private int numLights = 1;
    
    public final String name;
    private static Texture iconTexture;
    private Skybox skybox;
    
    /**
     * A collection of every {@link Entity} object in the scene.
     * <br><br>
     * Because the engine actively makes use of this collection elsewhere you should not attempt to supplant its functionality with 
     * your own. Instead the collection should be used to iterate through the logic loop of each entity like so:
     * <blockquote><pre>
     * <b>//In the scenes update() method...</b>
     * entities.values().forEach(entity -&gt; {
     *     entity.update();
     * });
     * 
     * <b>//In the scenes render() method...</b>
     * entities.values().foreach(entity -&gt; {
     *     entity.render();
     * });
     * </pre></blockquote>
     * 
     * The details regarding how this is achieved are largely subject to the needs of the implementation, though generally 
     * speaking for a large volume of entity objects lambda expressions like those above are usually sufficient enough.
     */
    protected final LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
    
    private final LightSource[] lightSources     = new LightSource[MAX_LIGHTS];
    private final LightSource[] lightSourcesCopy = new LightSource[MAX_LIGHTS];
    
    /**
     * Creates a new 3D scene that will contain entities, light sources, and camera objects.
     * 
     * @param name the name used to refer to the scene in other parts of the engine
     */
    public Scene(String name) {
        this.name = name;
        lightSources[0] = new LightSource(true, Light.daylight(), iconTexture);
    }
    
    /**
     * Calculates the current number of light source objects that currently exist in the scene. This includes light sources which are in
     * a disabled state.
     */
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource source : lightSources) {
            if(source != null) numLights++;
        }
    }
    
    /**
     * Supplies the scene with the texture atlas containing icons that will be used to render the positions of {@link LightSource} 
     * objects in debug mode.
     * 
     * @param iconTexture the texture atlas containing all the icons used by the engine
     */
    static void setIconTexture(Texture iconTexture) {
        Scene.iconTexture = iconTexture;
    }
    
    /**
     * Safely removes entity objects from the scenes {@link entities} collection. This method is called automatically by the engine.
     */
    void processRemoveRequests() {
        entities.entrySet().removeIf(entry -> entry.getValue().removalRequested());
    }
    
    /**
     * Updates the position of the scenes light source objects. This method is called automatically by the engine.
     */
    void updateLightSources() {
        for(LightSource source : lightSources) {
            if(source != null) source.update();
        }
    }
    
    /**
     * Renders the scenes current {@link Skybox} (if one exists). This method is called automatically by the engine. 
     * 
     * @param viewMatrix the view matrix of the viewport whos camera is currently rendering the scene
     */
    void renderSkybox(Matrix4f viewMatrix) {
        if(skybox != null) skybox.render(viewMatrix);
    }
    
    /**
     * Renders the icons indicating the positions of every initialized light source in the scene while debug mode is enabled. This method 
     * is called automatically by the engine.
     * 
     * @param camera the {@link Camera} object of the current {@link Viewport} being rendered
     */
    void renderLightsources(Camera camera) {
        if(XJGE.getLightSourcesVisible()) {
            for(LightSource source : lightSources) {
                if(source != null) source.render(camera.position, camera.direction, camera.up);
            }
        }
    }
    
    /**
     * Sets the current skybox the scene will render as part of its background.
     * 
     * @param skybox the completed skybox object to render
     */
    protected final void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }
    
    /**
     * Adds a new light to the scene. If the maximum number of lights has been reached, it will recycle the first one in sequence.
     * 
     * @param light the light object to add
     */
    protected final void addLight(Light light) {
        boolean search = true;
        
        for(int i = 1; search; i++) {
            if(i < MAX_LIGHTS) {
                if(lightSources[i] != null) {
                    if(!lightSources[i].getEnabled()) {
                        lightSources[i] = new LightSource(false, light, lightSources[i]);
                    }
                } else {
                    lightSources[i] = new LightSource(false, light, iconTexture);
                    search = false;
                }
            } else {
                currLightIndex = (currLightIndex == MAX_LIGHTS - 1) ? 1 : currLightIndex + 1;
                lightSources[currLightIndex] = new LightSource(false, light, lightSources[currLightIndex]);
                search = false;
            }
        }
        
        findNumLights();
    }
    
    /**
     * Adds a new light to the scene at the index specified. This is useful in situations where we would like to explicitly partition the 
     * lights in the scene for different uses.
     * <br><br>
     * If a light source already exists at that index, it will be replaced. Passing 
     * zero in place of the index value will change the current world light that illuminates all objects.
     * 
     * @param index an existing location within the light array to occupy
     * @param light the light object to add
     * 
     * @see MAX_LIGHTS
     */
    protected final void addLightAtIndex(int index, Light light) {
        try {
            if(light == null) {
                throw new NullPointerException();
            } else {
                lightSources[index] = new LightSource(index == 0, light, lightSources[index]);
                findNumLights();
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            Logger.setDomain("core");
            Logger.logWarning("Failed to add light object at index " + index, e);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Obtains the current amount of initialized light source objects within the scene.
     * 
     * @return the number of initialized light source objects
     */
    protected int getNumLights() {
        return numLights;
    }
    
    /**
     * Obtains a copy of the current light source array used by the scene. This cannot be used to alter light source objects but is 
     * instead intended to supply objects which exhibit shading (such as {@linkplain dev.theskidster.xjge2.graphics.Model 3D models}) 
     * with lighting data.
     * 
     * @return an array of light source objects
     */
    protected LightSource[] getLightSources() {
        System.arraycopy(lightSources, 0, lightSourcesCopy, 0, numLights);
        return lightSourcesCopy;
    }
    
    /**
     * Organizes and updates the game logic of the scene and every entity inhabiting it. Called automatically by the engine once every 
     * game tick. 
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta   the actual time (in seconds) it took the current game
     *                    tick to complete.
     */
    public abstract void update(double targetDelta, double trueDelta);
    
    /**
     * Organizes calls to the OpenGL API made by entities and other various objects located in the game world. This method is called 
     * automatically by the engine.
     * 
     * @param glPrograms an immutable collection containing the shader programs compiled during startup
     * @param viewportID the ID number of the viewport currently rendering the scene. Supplied here in case certain objects are to be
     *                   included or omitted from the viewports rendering pass
     * @param camera     the {@link Camera} object of the current {@link Viewport} being rendered
     */
    public abstract void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera);
    
    /**
     * Called by the engine when this scene is left for another. Any memory/resources allocated by the scene including that of entities 
     * which are no longer needed should be freed here.
     */
    public abstract void exit();
    
}