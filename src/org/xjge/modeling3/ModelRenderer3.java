package org.xjge.modeling3;

import java.util.LinkedList;
import static org.lwjgl.opengl.GL30.*;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelRenderer3 extends EntityComponent {

    private Model3 model;
    
    private Texture albedoMap;
    private Texture normalMap;
    private Texture metallicMap;
    private Texture roughnessMap;
    
    private static final Shader shader;
    
    static {
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("xjge_shader_model_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("xjge_shader_model_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        shader = new Shader(shaderSourceFiles, "xjge_model");
    }
    
    public ModelRenderer3(Model3 model) {
        this.model = model;
    }
    
    public void render(Camera camera) {
        
    }
    
}