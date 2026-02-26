package org.xjge.core;

import java.util.LinkedList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class ShadowMap2 {

    private int fbo;
    private int textureWidth;
    private int textureHeight;
    private int textureHandle;
    public int PCFValue;
    
    public float minBias;
    public float maxBias;
    public float frustumSize;
    public float range;
    
    private boolean useLinearFilter;
    
    private final Vector3f lightDir  = new Vector3f();
    private final Matrix4f lightView = new Matrix4f();
    private final Matrix4f lightProj = new Matrix4f();
    
    private final Matrix4f lightSpace = new Matrix4f();
    
    private static final Shader shader;
    
    static {
        var shaderStages = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("xjge_shader_depth_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("xjge_shader_depth_fragment.glsl", GL_FRAGMENT_SHADER));
        }};

        shader = new Shader(shaderStages, "xjge_depth");
    }
    
    void generate() {
        
    }
    
    void render() {
        
    }
    
    public void bindTexture() {
        
    }
    
}