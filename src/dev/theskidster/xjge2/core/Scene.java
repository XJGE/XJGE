package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import dev.theskidster.xjge2.graphics.Light;
import dev.theskidster.xjge2.graphics.Texture;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public abstract class Scene {

    public static final int MAX_LIGHTS = 32;
    
    private int currLightIndex;
    private int numLights = 1;
    
    public final String name;
    private static Texture iconTexture;
    
    protected final LinkedHashMap<String, Entity> entities = new LinkedHashMap<>();
    
    private final LightSource[] lightSources = new LightSource[MAX_LIGHTS];
    
    public Scene(String name) {
        this.name = name;
        lightSources[0] = new LightSource(true, Light.daylight(), iconTexture);
    }
    
    static void setIconTexture(Texture iconTexture) {
        Scene.iconTexture = iconTexture;
    }
    
    void processRemoveRequests() {
        entities.entrySet().removeIf(entry -> entry.getValue().removalRequested());
    }
    
    void updateLightSources() {
        for(LightSource source : lightSources) {
            if(source != null) source.update();
        }
    }
    
    void renderLightsources(Camera camera) {
        if(XJGE.getLightSourcesVisible()) {
            for(LightSource source : lightSources) {
                if(source != null) source.render(camera.position, camera.direction, camera.up);
            }
        }
    }
    
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource source : lightSources) {
            if(source != null) numLights++;
        }
    }
    
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
    
    protected final void addLightAtIndex(int index, Light light) {
        try {
            if(light == null) {
                throw new NullPointerException();
            } else {
                lightSources[index] = new LightSource(index == 0, light, lightSources[index]);
                findNumLights();
            }
        } catch(Exception e) {
            Logger.setDomain("core");
            Logger.logWarning("Failed to add light object at index " + index, e);
            Logger.setDomain(null);
        }
    }
    
    public abstract void update(double targetDelta);
    
    /*
    passed the viewport id here in case we only want to render certain objects 
    on one viewport.
    */
    public abstract void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera);
    
    public abstract void exit();
    
}