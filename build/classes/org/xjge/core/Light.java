package org.xjge.core;

import org.xjge.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.graphics.Atlas;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;

/**
 * Created: Jun 12, 2021
 * <p>
 * Represents a source of visible light at some point in 3D space. This can be 
 * best understood as a physical object that emits light, such as a light bulb.
 * <p>
 * This data structure is used internally by the engine for its own lighting 
 * utilities.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Light {

    public boolean enabled = true;
    
    public float brightness;
    public float contrast;
    public float distance;
    
    public Vector3f position;
    public Color ambientColor;
    public Color diffuseColor;
    public Color specularColor;
    
    private Graphics g;
    private static Texture iconTexture;
    private Atlas atlas;
    private Vector2f texCoords;
    
    /**
     * Creates a new light object that contains data which can be used by a 
     * fragment shader during lighting calculations.
     * 
     * @param brightness the intensity of the light
     * @param contrast the noticeable difference between the intensity of the 
     *                 ambient and diffuse colors of this light. Should be a 
     *                 non-negative value between 0 and 1.
     * @param distance the range of the lights influence. NOTE: this value may 
     *                 not correlate to world space perfectly.
     * @param position the position from which the light will be emitted
     * @param ambientColor the color that will be used to shade the side of the 
     *                     entity which is not facing the light source 
     * @param diffuseColor the color that will be dispersed into nearby entity 
     *                     from the general direction of the light source
     * @param specularColor the color that will be reflected off entities 
     *                      depending on their shininess value
     */
    public Light(float brightness, float contrast, float distance, Vector3f position, Color ambientColor, Color diffuseColor, Color specularColor) {        
        this.brightness    = Math.abs(brightness);
        this.contrast      = XJGE.clampValue(0, 1, contrast);
        this.distance      = XJGE.clampValue(0, Float.MAX_VALUE, distance);
        this.position      = position;
        this.ambientColor  = ambientColor;
        this.diffuseColor  = diffuseColor;
        this.specularColor = specularColor;
        
        g         = new Graphics();
        atlas     = new Atlas(iconTexture, 64, 64);
        texCoords = new Vector2f(atlas.subImageWidth * 2, atlas.subImageHeight);
        
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
     * Variant of {@link Light(float, float, float, Vector3f, Color, Color, Color)} 
     * that will initialize each color used in the Phong lighting model to the same value.
     * 
     * @param brightness the intensity of the light
     * @param contrast the noticeable difference between the intensity of the 
     *                 ambient and diffuse colors of this light. Should be a 
     *                 non-negative value between 0 and 1.
     * @param distance the range of the lights influence. NOTE: this value may 
     *                 not correlate to world space perfectly.
     * @param position the position from which the light will be emitted
     * @param color the color that each Phong component (ambient, diffuse, specular) 
     *              will be set to
     */
    public Light(float brightness, float contrast, float distance, Vector3f position, Color color) {
        this(brightness, contrast, distance, position, color, color, color);
    }
    
    /**
     * Generates a random number between the specified minimum and maximum values.
     * 
     * @param minValue the minimum value this method may return
     * @param maxValue the maximum value this method may return
     * 
     * @return a random floating point number between the specified values
     */
    private static float randomValue(float minValue, float maxValue) {
        return (float) (minValue + Math.random() * (maxValue - minValue));
    }
    
    /**
     * Supplies the light class with a texture containing icons that will 
     * indicate individual light positions within the current scene.
     * 
     * @param engineIcons the texture atlas containing all the icons used by the engine
     */
    static void setIconTexture(Texture engineIcons) {
        iconTexture = engineIcons;
    }
    
    /**
     * Updates the current position and icon of the light object.
     *
     * @param index a unique number used to identify the light
     */
    void update(int index) {
        g.modelMatrix.translation(position);
        
        if(index == 0) texCoords.set(atlas.subImageWidth, atlas.subImageHeight);
        else           texCoords.set(atlas.subImageWidth * 2, atlas.subImageHeight);
    }
    
    /**
     * Renders an icon representing the current position of the light source in 
     * the game world. Light source visibility can be toggled by pressing F3 
     * while debug mode is enabled.
     * 
     * @param camPos the current position of the viewports camera object
     * @param camDir the direction in which the viewports camera is currently facing
     * @param camUp the direction considered upwards relative to the viewports camera
     */
    void render(Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(position, camPos, camUp);
        g.modelMatrix.scale(camPos.distance(position) / 10);

        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        glActiveTexture(GL_TEXTURE0);
        iconTexture.bind(GL_TEXTURE_2D);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 6);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uColor", diffuseColor.asVec3());
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", texCoords);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * mimics daylight.
     * 
     * @return a new engine-defined light object
     */
    public static final Light daylight() {
        return new Light(1, 0.55f, 1, new Vector3f(-10, 30, 7.5f), Color.WHITE);
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * simulates the hue of a sunset.
     * 
     * @return a new engine-defined light object
     */
    public static final Light sunset() {
        return new Light(0.87f, 0.5f, 1, new Vector3f(-8, 7.5f, -30), Color.create(173, 141, 162), Color.create(255, 204, 86), Color.create(255, 234, 184));
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * resembles moonlight.
     * 
     * @return a new engine-defined light object
     */
    public static final Light midnight() {
        return new Light(0.82f, 0.55f, 1, new Vector3f(6.5f, 16.7f, 30), Color.create(38, 48, 76), Color.create(48, 62, 80), Color.create(188, 209, 231));
    }
    
    /**
     * Provides implementing applications with a predefined light object that 
     * resembles a red air traffic beacon.
     * 
     * @return a new engine-defined light object
     */
    public static final Light beacon() {
        return new Light(2.5f, 1, 1.25f, new Vector3f(), Color.RED);
    }
    
    /**
     * Provides implementing applications with a predefined light object that 
     * resembles a bright green glowstick. NOTE: This light does not produce 
     * specular highlights by design. 
     * 
     * @return a new engine-defined light object
     */
    public static final Light glowstick() {
        return new Light(0.8f, 0.7f, 0.52f, new Vector3f(), Color.GREEN, Color.GREEN, Color.BLACK);
    }
    
    /**
     * Provides implementing applications with a predefined light object that 
     * resembles a cryogenic storage device.
     * 
     * @return a new engine-defined light object
     */
    public static final Light cryotube() {
        return new Light(3.97f, 0.84f, 1.5f, new Vector3f(), Color.create(192, 8, 238), Color.create(10, 90, 153), Color.create(10, 46, 8));
    }
    
    /**
     * Provides implementing applications with a predefined light object that 
     * resembles the warm glow of a campfire.
     * 
     * @return a new engine-defined light object
     */
    public static final Light campfire() {
        return new Light(6.2f, 0.45f, 0.62f, new Vector3f(), Color.create(11, 71, 231), Color.create(209, 85, 3), Color.create(145, 140, 149));
    }
    
    /**
     * Provides implementing applications with a randomized light object.
     * 
     * @return a new engine-generated light object
     */
    public static final Light random() {
        return new Light((float) Math.log(randomValue(0.5f, 10)), randomValue(0, 1), randomValue(0, 5), 
                          new Vector3f(), 
                          Color.random(), Color.random(), Color.random());
    }
    
}