package org.xjge.core;

import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

//Created: May 7, 2021

/**
 * A 3D representation of the game world that contains entities, light sources, 
 * and camera objects.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see Entity
 * @see LightSource
 * @see Camera
 */
public abstract class Scene {

    /**
     * The maximum number of light source objects that may be simultaneously 
     * present in the scene at any given time.
     */
    public static final int MAX_LIGHTS = 32;
    
    private int currLightIndex;
    private int numLights = 1;
    
    public final String name;
    private static Texture iconTexture;
    private Skybox skybox;
    private ShadowMap shadowMap;
    
    private static final Vector3f noValue = new Vector3f();
    
    /**
     * A collection of every {@link Entity} object in the scene.
     * <p>
     * Because the engine actively makes use of this collection elsewhere you 
     * should not attempt to supplant its functionality with your own. Instead 
     * the collection should be used to iterate through the logic loop of each 
     * entity like so:
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
     * <p>
     * The details regarding how this is achieved are largely subject to the 
     * needs of the implementation, though generally speaking for a large 
     * volume of entity objects lambda expressions like those shown above are 
     * usually sufficient for most cases.
     */
    protected final LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
    
    public final Light[] lights = new Light[MAX_LIGHTS];
    
    /**
     * Creates a new 3D scene that will contain entities, light sources, and 
     * camera objects.
     * 
     * @param name the name used to refer to the scene in other parts of the 
     *             engine
     */
    public Scene(String name) {
        this.name = name;
        lights[0] = Light.daylight();
    }
    
    /**
     * Calculates the current number of light source objects that currently 
     * exist in the scene. This includes light sources which are in a disabled 
     * state.
     */
    private void findNumLights() {
        numLights = 1;
        
        for(int i = 1; i < MAX_LIGHTS; i++) {
            if(lights[i] != null) numLights++;
        }
    }
    
    /**
     * Supplies the scene with the texture atlas containing icons that will be 
     * used to render the positions of {@link LightSource} objects in debug 
     * mode.
     * 
     * @param iconTexture the texture atlas containing all the icons used by 
     *                    the engine
     */
    static void setIconTexture(Texture iconTexture) {
        Scene.iconTexture = iconTexture;
    }
    
    /**
     * Safely removes entity objects from the scenes {@link entities} 
     * collection. This method is called automatically by the engine.
     */
    void processRemoveRequests() {
        entities.entrySet().removeIf(entry -> entry.getValue().removalRequested());
    }
    
    /**
     * Renders the scenes current {@link Skybox} (if one exists). This method 
     * is called automatically by the engine. 
     * 
     * @param viewMatrix the view matrix of the viewport whos camera is 
     *                   currently rendering the scene
     */
    void renderSkybox(Matrix4f viewMatrix) {
        if(skybox != null) skybox.render(viewMatrix);
    }
    
    /**
     * Renders the icons indicating the positions of every initialized light 
     * source in the scene while debug mode is enabled. This method is called 
     * automatically by the engine.
     * 
     * @param camera the {@link Camera} object of the current {@link Viewport} 
     *               being rendered
     */
    void renderLightSources(Camera camera) {
        if(XJGE.getLightSourcesVisible()) {
            for(LightSource source : lightSources) {
                if(source != null) source.render(camera.position, camera.direction, camera.up);
            }
        }
    }
    
    /**
     * Calculates the shadows cast by each entity provided to the 
     * {@linkplain entities} collection. This method is called automatically 
     * by the engine.
     * 
     * @param depthProgram the shader program provided by the engine that will 
     *                     be used to generate the shadow map texture
     */
    void renderShadows(Vector3f camUp, GLProgram depthProgram) {
        if(shadowMap != null) {
            shadowMap.generate(camUp, depthProgram, lights[0].position, entities);
        }
    }
    
    void setShadowUniforms() {
        if(shadowMap != null) {
            XJGE.getDefaultGLProgram().setUniform("uLightSpace", false, shadowMap.lightSpace);
            XJGE.getDefaultGLProgram().setUniform("uPCFValue", shadowMap.PCFValue);
        }
    }
    
    /**
     * Supplies every light struct in the default fragment shader with uniform 
     * values from their corresponding {@link LightSource} objects. This 
     * method is called automatically by the engine.
     */
    void setLightingUniforms() {
        for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
            if(lights[i] != null) {
                if(lights[i].enabled) {
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].brightness", lights[i].brightness);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].contrast",   lights[i].contrast);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].distance",   lights[i].distance);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].position",   lights[i].position);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].ambient",    lights[i].ambientColor.asVec3());
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].diffuse",    lights[i].diffuseColor.asVec3());
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].specular",   lights[i].specularColor.asVec3());
                } else {
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].brightness", 0);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].contrast",   0);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].distance",   0);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].position",   noValue);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].ambient",    noValue);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].diffuse",    noValue);
                    XJGE.getDefaultGLProgram().setUniform("uLights[" + i + "].specular",   noValue);
                }
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
     * Adds a new light to the scene. If the maximum number of lights has been 
     * reached, it will recycle the first one in sequence.
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
     * Adds a new light to the scene at the index specified. This is useful in 
     * situations where we would like to explicitly partition the lights in the 
     * scene for different uses.
     * <p>
     * If a light source already exists at that index, it will be replaced. 
     * Passing zero in place of the index value will change the current world 
     * light that illuminates all objects.
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
     * Obtains the current amount of initialized light source objects within 
     * the scene.
     * 
     * @return the number of initialized light source objects
     */
    protected int getNumLights() {
        return numLights;
    }
    
    /**
     * Organizes and updates the game logic of the scene and every entity 
     * inhabiting it. Called automatically by the engine once every game tick. 
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta   the actual time (in seconds) it took the current game
     *                    tick to complete.
     */
    public abstract void update(double targetDelta, double trueDelta);
    
    /**
     * Organizes calls to the OpenGL API made by entities and other various 
     * objects located in the game world. This method is called automatically 
     * by the engine.
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     * @param viewportID the ID number of the viewport currently rendering the 
     *                   scene. Supplied here in case certain objects are to be
     *                   included or omitted from the viewports rendering pass
     * @param camera     the {@link Camera} object of the current 
     *                   {@link Viewport} being rendered
     */
    public abstract void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, Light[] lights, int numLights);
    
    /**
     * Called by the engine when this scene is left for another. Any 
     * memory/resources allocated by the scene including that of entities which 
     * are no longer needed should be freed here.
     */
    public abstract void exit();
    
}