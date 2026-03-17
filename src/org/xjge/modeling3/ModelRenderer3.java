package org.xjge.modeling3;

import java.util.LinkedList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.core.LightingSystem;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelRenderer3 extends EntityComponent {

    private Model3 model;
    
    private final Matrix3f normalMatrix = new Matrix3f();
    private final Matrix4f modelMatrix  = new Matrix4f();
    
    private static final Shader shader;
    
    static {
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("xjge_shader_model_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("xjge_shader_model_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        shader = new Shader(shaderSourceFiles, "xjge_model");
    }
    
    public ModelRenderer3(Model3 model) {
        setModel(model);
    }
    
    public final void setModel(Model3 model) {
        this.model = model;
    }
    
    public void render(Camera camera) {
        normalMatrix.set(modelMatrix).invert().transpose();
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        shader.use();
        
        shader.setUniform("uModel", false, modelMatrix); //should this be the models rootTransform?
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uNormalMatrix", true, normalMatrix);
        shader.setUniform("uCamPos", camera.position);
        shader.setUniform("uNumLights", LightingSystem.getActiveCount());
        
        //TODO: upload bone transform data
        
        for(var mesh : model.getMeshes()) {
            var material = model.getMaterials().get(mesh.materialIndex);
            
            shader.setUniform("uMaterial.metallic", material.metallic);
            shader.setUniform("uMaterial.roughness", material.roughness);
            shader.setUniform("uMaterial.albedoColor", material.albedoColor);
            
            int unit = 0;
            
            ErrorUtils.checkGLError();
            
            if(material.albedoMapFilename != null) {
                glActiveTexture(GL_TEXTURE0 + unit);
                material.albedoMap.bind(GL_TEXTURE_2D);
                shader.setUniform("uMaterial.albedoMap", unit);
                shader.setUniform("uHasAlbedoMap", 1);
            } else {
                shader.setUniform("uHasAlbedoMap", 0);
            }
            
            glBindVertexArray(mesh.vao);
            glDrawElements(GL_TRIANGLES, mesh.indices.length, GL_UNSIGNED_INT, 0);
        }
        
        glBindVertexArray(0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}