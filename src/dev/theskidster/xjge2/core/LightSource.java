package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.Atlas;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.graphics.Light;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

/**
 * Represents a source of visible light at some point in space. This object can be best conceptualized as a physical object that emits 
 * light, such as a light bulb.
 * <br><br>
 * This is a data structure used internally by the engine. If you wish to add a new source of light to a scene, you should use either
 * {@link Game#addLight(dev.theskidster.xjge2.graphics.Light) Game.addLight()} or 
 * {@link Scene#addLight(dev.theskidster.xjge2.graphics.Light) Scene.addLight()}.
 */
public final class LightSource {
    
    private final boolean isWorldLight;
    
    private final Light light;
    private final Graphics g;
    private final Texture iconTexture;
    private final Atlas atlas;
    private final Vector2f texCoords;
    
    /**
     * Creates a new object that represents a source of light in 3D space.
     * 
     * @param isWorldLight if true, this light source object will be designated as the world light source
     * @param light        the light data structure that will be coupled to this light source object
     * @param iconTexture  the texture atlas containing all the icons used by the engine
     */
    LightSource(boolean isWorldLight, Light light, Texture iconTexture) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        this.iconTexture  = iconTexture;
        
        g         = new Graphics();
        atlas     = new Atlas(iconTexture, 64, 64);
        texCoords = new Vector2f();
        
        setTexCoords();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-0.5f) .put(0.5f).put(0) .put(0)                  .put(0);
            g.vertices .put(0.5f) .put(0.5f).put(0) .put(atlas.subImageWidth).put(0);
            g.vertices .put(0.5f).put(-0.5f).put(0) .put(atlas.subImageWidth).put(atlas.subImageHeight);
            g.vertices.put(-0.5f).put(-0.5f).put(0) .put(0)                  .put(atlas.subImageHeight);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    /**
     * Creates a new light source object from an existing one.
     * 
     * @param isWorldLight if true, this light source object will be designated as the world light source
     * @param light        the light data structure that will be coupled to this light source object 
     * @param source       the light source object to copy
     */
    LightSource(boolean isWorldLight, Light light, LightSource source) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        
        g           = source.g;
        iconTexture = source.iconTexture;
        atlas       = source.atlas;
        texCoords   = source.texCoords;
        
        setTexCoords();
    }
    
    /**
     * Determines which icon the light source object will utilize during debugging.
     */
    private void setTexCoords() {
        if(isWorldLight) texCoords.set(atlas.subImageWidth, atlas.subImageHeight);
        else             texCoords.set(atlas.subImageWidth * 2, atlas.subImageHeight);
    }
    
    /**
     * Updates the current position of the light source.
     */
    void update() {
        g.modelMatrix.translation(light.position);
    }
    
    /**
     * Renders an icon representing the current position of the light source in the game world. Light source visibility can be toggled by 
     * pressed F3 while debug mode is enabled.
     * 
     * @param camPos the current position of the viewports camera object
     * @param camDir the direction in which the viewports camera is currently facing
     * @param camUp  the direction considered upwards relative to the viewports camera
     */
    void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);
        g.modelMatrix.scale(camPos.distance(light.position) / 10);

        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, iconTexture.handle);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 6);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uColor", light.ambientColor.asVec3());
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", texCoords);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    /**
     * Obtains the current enabled value of the source objects light struct. This value essentially acts as an "on/off" switch with 
     * enabled lights casting light on nearby objects that exhibit shading.
     * 
     * @return true if the light is currently enabled
     */
    public boolean getEnabled() {
        return light.enabled;
    }
    
    /**
     * Obtains the brightness value of the source objects light struct. This value denotes the intensity and range of the light.
     * 
     * @return the brightness value of the source objects light struct
     */
    public float getBrightness() {
        return light.brightness;
    }
    
    /**
     * Obtains the contrast value of the source objects light struct. This value controls the contrast between the ambient and diffuse
     * colors on nearby objects that exhibit shading.
     * 
     * @return the contrast value of the source objects light struct
     */
    public float getContrast() {
        return light.contrast;
    }
    
    /**
     * Obtains the current position of this source objects light struct in 3D space. This value sets the position from which the light 
     * will be emitted.
     * 
     * @return the position value of the source objects light struct
     */
    public Vector3f getPosition() {
        return light.position;
    }
    
    /**
     * Obtains the ambient color of this source objects light struct. The ambient color determines the brightness and hue of nearby 
     * objects edges that face away from the light.
     * 
     * @return the color value of the ambient lighting
     */
    public Vector3f getAmbientColor() {
        return light.ambientColor.asVec3();
    }
    
    /**
     * Obtains the diffuse color of this source objects light struct. The diffuse color is refracted by the edges of nearby objects that
     * face in the direction of the light.
     * 
     * @return the color that will be reflected by objects within range of the light
     */
    public Vector3f getDiffuseColor() {
        return light.diffuseColor.asVec3();
    }
    
}