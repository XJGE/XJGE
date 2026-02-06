package org.xjge.core;

import java.util.ArrayList;
import java.util.Collections;
import org.xjge.graphics.GLProgram;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.xjge.core.EntityChangeType.*;

/**
 * A 3D representation of the game world that contains entities, light sources, 
 * and camera objects.
 * <p>
 * NOTE: The {@link org.xjge.core.TCSetScene setScene} command will fail if 
 * the default constructor of a Scene subclass uses parameters. You should 
 * instead set the value of the scenes {@code name} field explicitly with the 
 * {@code super} keyword.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see Entity
 * @see Light
 * @see Camera
 * @see Skybox
 * @see ShadowMap
 */
public abstract class Scene {

    /**
     * The maximum number of light source objects that may be simultaneously 
     * present in the scene at any given time.
     */
    public static final int MAX_LIGHTS = 32;
    
    public final String name;
    private Skybox skybox;
    private ShadowMap shadowMap;
    
    private static final Vector3f noValue = new Vector3f();
    
    private final Map<UUID, Entity> entities = new HashMap<>();
    
    private final Map<UUID, EntityChangeRequest> entityChangeRequests = new HashMap<>();
    private final Map<UUID, EntitySignature> entitySignatures         = new HashMap<>();
    private final Map<EntitySignature, List<Entity>> entityArchetypes = new HashMap<>();
    
    /**
     * An array that contains every {@link Light} object currently present 
     * within the scene.
     * <p>
     * Like the {@linkplain entities entities collection} the engine makes use
     * of this array in other places- however it permits a greater degree of 
     * freedom as the values contained by the light objects are generic and can 
     * be used for custom lighting solutions should the implementing application 
     * decide not to utilize the engines built-in lighting utilities.
     * <p>
     * NOTE: When using the engines built-in lighting utilities the index of 
     * zero is reserved for the scenes global light source. This light source 
     * will illuminate all entities within the scene regardless of their  
     * physical location or proximity to the light. Additionally, if a shadow 
     * map is present, it will project its frustum in the direction of the 
     * origin from the global light source. Any light object placed at the index 
     * of zero will instantly assume the role of the global light source.
     */
    protected final Light[] lights = new Light[MAX_LIGHTS];
    
    /**
     * Creates a new 3D scene that will contain entities, light sources, and 
     * camera objects.
     * 
     * @param name the name used to refer to the scene in other parts of the engine
     */
    public Scene(String name) {
        this.name = name;
    }
    
    /**
     * Used to validate whether a shader program has defined a uniform variable
     * that can be used to store light data provided by the engine.
     * 
     * @param glProgram the program to query
     * @param name the name of the uniform variable to check for
     * @param value the value to pass to the uniform variable
     */
    private void setLightUniform(GLProgram glProgram, String name, float value) {
        if(glProgram.containsUniform(name)) {
            glProgram.use();
            glProgram.setUniform(name, value);
        }
    }
    
    /**
     * Variant of {@link setLightUniform(GLProgram, String, float) setLightProgram()} 
     * that accepts a 3-component floating point vector.
     * 
     * @param glProgram the program to query
     * @param name the name of the uniform variable to check for
     * @param value the value to pass to the uniform variable
     */
    private void setLightUniform(GLProgram glProgram, String name, Vector3f value) {
        if(glProgram.containsUniform(name)) {
            glProgram.use();
            glProgram.setUniform(name, value);
        }
    }
    
    /**
     * Supplies every light struct in the default fragment shader with uniform 
     * values from their corresponding {@link Light} objects. Custom shaders 
     * may also utilize this data provided they include a light struct and 
     * corresponding uniform array that exhibits the following structure;
     * <blockquote><pre>
     * struct Light {
     *     float brightness;
     *     float contrast;
     *     float distance;
     *     vec3 position;
     *     vec3 ambient;
     *     vec3 diffuse;
     *     vec3 specular;
     * };
     * 
     * ...
     * 
     * uniform Light uLights[32]; //Must be no larger than 32!
     * </pre></blockquote>
     * This method is called automatically by the engine.
     */
    void setLightingUniforms() {
        XJGE.glPrograms.values().forEach(glProgram -> {
            for(int i = 0; i < Scene.MAX_LIGHTS; i++) {                
                if(lights[i] != null) {
                    if(lights[i].enabled) {
                        setLightUniform(glProgram, "uLights[" + i + "].brightness", lights[i].brightness);
                        setLightUniform(glProgram, "uLights[" + i + "].contrast",   lights[i].contrast);
                        setLightUniform(glProgram, "uLights[" + i + "].distance",   lights[i].distance);
                        setLightUniform(glProgram, "uLights[" + i + "].position",   lights[i].position);
                        setLightUniform(glProgram, "uLights[" + i + "].ambient",    lights[i].ambientColor.asVector3f());
                        setLightUniform(glProgram, "uLights[" + i + "].diffuse",    lights[i].diffuseColor.asVector3f());
                        setLightUniform(glProgram, "uLights[" + i + "].specular",   lights[i].specularColor.asVector3f());
                    } else {
                        setLightUniform(glProgram, "uLights[" + i + "].brightness", 0);
                        setLightUniform(glProgram, "uLights[" + i + "].contrast",   0);
                        setLightUniform(glProgram, "uLights[" + i + "].distance",   0);
                        setLightUniform(glProgram, "uLights[" + i + "].position",   noValue);
                        setLightUniform(glProgram, "uLights[" + i + "].ambient",    noValue);
                        setLightUniform(glProgram, "uLights[" + i + "].diffuse",    noValue);
                        setLightUniform(glProgram, "uLights[" + i + "].specular",   noValue);
                    }
                }
            }
        });
    }
    
    /**
     * Updates the current position and icon of every initialized light object.
     * This method is called automatically by the engine.
     */
    void updateLightSources() {
        if(XJGE.getLightSourcesVisible()) {
            for(int i = 0; i < MAX_LIGHTS; i++) {
                if(lights[i] != null) lights[i].update(i);
            }
        }
    }
    
    /**
     * Renders the icons indicating the positions of every initialized light 
     * object in the scene while debug mode is enabled. This method is called 
     * automatically by the engine.
     * 
     * @param camera the {@link Camera} object of the current {@link Viewport} 
     *               being rendered
     */
    void renderLightSources(Camera camera) {
        if(XJGE.getLightSourcesVisible()) {
            for(Light light : lights) {
                if(light != null) light.render(camera.position, camera.direction, camera.up);
            }
        }
    }
    
    /**
     * Supplies the values from the fields of the current {@link ShadowMap} 
     * object to their corresponding uniform variables in the default fragment 
     * shader and any shader program provided to the engine by the 
     * implementation that wishes to capture shadow map data. Custom shaders can 
     * include or omit uniforms as they see fit however the names and data types 
     * of the uniforms within the program themselves <i>must</i> conform to the 
     * layout specified below;
     * <table><caption></caption>
     * <tr><td><b>NAME:</b></td><td><b>TYPE:</b></td></tr>
     * <tr><td>uLightSpace</td><td>mat4</td></tr>
     * <tr><td>uPCFValue</td><td>int</td></tr>
     * <tr><td>uMinShadowBias</td><td>float</td></tr>
     * <tr><td>uMaxShadowBias</td><td>float</td></tr>
     * <tr><td>uShadowMapActive</td><td>int</td></tr>
     * </table>
     * <p>
     * This method is called automatically by the engine.
     */
    void setShadowUniforms() {
        if(shadowMap != null) {
            XJGE.glPrograms.values().forEach(glProgram -> {
                for(int i = 0; i < 5; i++) {
                    String uniformName = switch(i) {
                        case 1  -> "uPCFValue";
                        case 2  -> "uMinShadowBias";
                        case 3  -> "uMaxShadowBias";
                        case 4  -> "uShadowMapActive";
                        default -> "uLightSpace";
                    };
                    
                    if(glProgram.containsUniform(uniformName)) {
                        glProgram.use();
                        
                        switch(uniformName) {
                            case "uLightSpace"      -> glProgram.setUniform("uLightSpace", false, shadowMap.lightSpace);
                            case "uPCFValue"        -> glProgram.setUniform("uPCFValue", shadowMap.PCFValue);
                            case "uMinShadowBias"   -> glProgram.setUniform("uMinShadowBias", shadowMap.minBias);
                            case "uMaxShadowBias"   -> glProgram.setUniform("uMaxShadowBias", shadowMap.minBias);
                            case "uShadowMapActive" -> glProgram.setUniform("uShadowMapActive", 1);
                        }
                    }
                }
            });
        } else {
            XJGE.glPrograms.values().forEach(glProgram -> {
                if(glProgram.containsUniform("uShadowMapActive")) glProgram.setUniform("uShadowMapActive", 0);
            });
        }
    }
    
    /**
     * Sets the current shadow map that will be used to cast shadows onto 
     * various entities within the scene.
     * 
     * @param shadowMap the shadow map object to use
     */
    protected final void setShadowMap(ShadowMap shadowMap) {
        this.shadowMap = shadowMap;
    }
    
    /**
     * Calculates the shadows cast by each entity provided to the 
     * {@linkplain entities} collection. This method is called automatically 
     * by the engine.
     * 
     * @param depthProgram the shader program provided by the engine that will 
     *                     be used to generate the shadow map texture
     */
    void renderShadowMap(Vector3f camUp, GLProgram depthProgram) {
        if(shadowMap != null && lights[0] != null) {
            shadowMap.generate(camUp, depthProgram, this);
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
     * Renders the scenes current {@link Skybox} (if one exists). This method 
     * is called automatically by the engine. 
     * 
     * @param camera the camera object of the viewport the scene is currently being rendered from
     */
    void renderSkybox(Camera camera) {
        if(skybox != null) skybox.render(camera.viewMatrix, camera.projMatrix);
    }
    
    /**
     * Variant of the 
     * {@linkplain render(Map, int, Camera, int) render()} method that's 
     * intentionally kept beyond the implementations reach and used to populate 
     * the value of the {@code depthTexHandle} argument. This method is called 
     * automatically by the engine. 
     * 
     * @param glPrograms an immutable collection containing the shader programs 
     *                   compiled during startup
     * @param viewportID the ID number of the viewport currently rendering the 
     *                   scene. Supplied here in case certain objects are to 
     *                   be included or omitted from the viewports rendering 
     *                   pass
     * @param camera the {@link Camera} object of the current {@link Viewport} 
     *               being rendered
     */
    void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        if(shadowMap != null) render(glPrograms, viewportID, camera, shadowMap.depthTexHandle);
        else                  render(glPrograms, viewportID, camera, -1);
    }
    
    /**
     * Changes requested during Scene.update() are always applied after the update cycle completes.
     * 
     * @param entity
     * @param type
     * @param subclass 
     */
    void queueChangeRequest(Entity entity, EntityChangeType type, Class<? extends EntityComponent> subclass) {
        var request    = entityChangeRequests.computeIfAbsent(entity.uuid, id -> new EntityChangeRequest());
        request.entity = entity;
        
        switch(type) {
            case ADD_ENTITY -> {
                request.addEntity    = true;
                request.removeEntity = false;
            }
            
            case REMOVE_ENTITY -> {
                request.addEntity    = false;
                request.removeEntity = true;
            }
            
            case ADD_COMPONENT -> {
                request.removeComponents.remove(subclass);
                request.addComponents.add(subclass);
            }
            
            case REMOVE_COMPONENT -> {
                request.addComponents.remove(subclass);
                request.removeComponents.add(subclass);
            }
        }
    }
    
    /**
     * Safely handles requests to add entities to this scene and attach any components used by them. This method is called 
     * automatically by the engine for internal use only.
     */
    void processChangeRequests() {
        for(var request : entityChangeRequests.values()) {
            if(request.removeEntity) {
                entities.remove(request.entity.uuid);
                var signature = entitySignatures.remove(request.entity.uuid);
                
                if(signature != null) {
                    var list = entityArchetypes.get(signature);
                    if(list != null) list.remove(request.entity);
                }
                
                continue;
            }
            
            if(request.addEntity) {
                entities.put(request.entity.uuid, request.entity);
                request.entity.currentScene = this;
                EntitySignature signature = new EntitySignature();
                
                for(EntityComponent c : request.entity.getAllComponents()) signature.add(c.getClass());

                EntitySignature frozen = signature.freeze(); //Prevent changes from occuring during update()
                entitySignatures.put(request.entity.uuid, frozen);

                entityArchetypes.computeIfAbsent(frozen, k -> new ArrayList<>()).add(request.entity);
            }
            
            for(Class<? extends EntityComponent> subclass : request.removeComponents) {
                request.changeComponent(false, entitySignatures, entityArchetypes, subclass);
            }
            
            for(Class<? extends EntityComponent> subclass : request.addComponents) {
                request.changeComponent(true, entitySignatures, entityArchetypes, subclass);
            }
        }
        
        entityChangeRequests.clear();
    }
    
    /**
     * Organizes and updates the game logic of the scene and every entity 
     * inhabiting it. Called automatically by the engine once every game tick. 
     * 
     * @param targetDelta a constant value denoting the desired time (in 
     *                    seconds) it should take for one game tick to complete
     * @param trueDelta the actual time (in seconds) it took the current game
     *                  tick to complete
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
     * @param camera the {@link Camera} object of the current {@link Viewport} 
     *               being rendered
     * @param depthTexHandle the handle of the texture generated by the current
     *                       shadow map or -1 if one has not been set
     */
    public abstract void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle);
    
    /**
     * Used to organize calls to the OpenGL API by entities and other objects 
     * within the scene who wish to cast shadows. This method is called 
     * automatically by the engine.
     * <p>
     * NOTE: The depth shader program is provided here exclusively by the engine 
     * and is not accessible through other means such as the {@code glPrograms} 
     * collection.
     * 
     * @param depthProgram the shader program provided by the engine that will 
     *                     be used to generate the shadow map texture
     */
    public abstract void renderShadows(GLProgram depthProgram);
    
    /**
     * Called by the engine when this scene is left for another. Any 
     * memory/resources allocated by the scene including that of entities which 
     * are no longer needed should be freed here.
     */
    public abstract void exit();
    
    /**
     * Safely adds an entity to this scene. More specifically this will queue a request to add the specified entity during the 
     * next game tick.
     * 
     * @param entity the entity object that will be added to this scene
     */
    public void addEntity(Entity entity) {
        queueChangeRequest(entity, ADD_ENTITY, null);
    }
    
    /**
     * Safely removes the entity with the specified UUID from this scene. More specifically this will queue a request to remove 
     * the entity during the next game tick.
     * 
     * @param uuid the unique identifier of the entity object we want removed from this scene
     */
    public void removeEntity(UUID uuid) {
        var entity = entities.get(uuid);
        if(entity != null) queueChangeRequest(entity, REMOVE_ENTITY, null);
    }
    
    public int entityCount() {
        return entities.size();
    }
    
    /**
     * 
     * @param uuid
     * @return the entity with the given UUID, or <code>null</code> if no such entity exists.
     */
    public Entity getEntity(UUID uuid) {
        return entities.get(uuid);
    }
    
    /**
     * Read-only copy of the entities collection containing all Entity objects currently in the scene.
     * 
     * @return 
     */
    public Iterable<Entity> allEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }
    
    /**
     * Obtains a subset of entities according to which components they currently have assigned.
     * 
     * @param components
     * @return 
     */
    public Iterable<Entity> queryEntities(Class<? extends EntityComponent>... components) {
        var required = new EntitySignature();
        for(var c : components) required.add(c);

        List<Entity> result = new ArrayList<>();
        
        for(var entry : entityArchetypes.entrySet()) {
            if(entry.getKey().containsAll(required)) result.addAll(entry.getValue());
        }
        
        return result;
    }
    
}