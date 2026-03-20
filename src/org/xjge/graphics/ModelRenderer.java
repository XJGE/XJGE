package org.xjge.graphics;

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
public class ModelRenderer extends EntityComponent {
    
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
    
    public void render(Model model, Transform transform, ModelAnimator animator, Camera camera) {
        //TODO: might be worthwhile to have transform store a matrix derived from this for performance
        modelMatrix.identity()
                   .translate(transform.position)
                   .rotate(transform.rotation)
                   .scale(transform.scale);
        
        normalMatrix.set(modelMatrix).invert().transpose();
        
        shader.use();
        
        shader.setUniform("uModel", false, modelMatrix);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uNormalMatrix", true, normalMatrix);
        shader.setUniform("uCamPos", camera.position);
        shader.setUniform("uNumLights", LightingSystem.getActiveCount());
        
        if(animator != null) {
            shader.setUniform("uBoneTransforms", false, animator.getFinalBoneMatrices());
        }
        
        for(var mesh : model.getMeshes()) {
            var material = model.getMaterials().get(mesh.materialIndex);
            
            shader.setUniform("uMaterial.metallic", material.metallic);
            shader.setUniform("uMaterial.roughness", material.roughness);
            shader.setUniform("uMaterial.albedoColor", material.albedoColor);
            
            int unit = 0;
            
            if(material.albedoMapFilename != null) {
                glActiveTexture(GL_TEXTURE0 + unit);
                material.albedoMap.bind(GL_TEXTURE_2D);
                shader.setUniform("uMaterial.albedoMap", unit);
                shader.setUniform("uHasAlbedoMap", 1);
                unit++;
            } else {
                shader.setUniform("uHasAlbedoMap", 0);
            }
            
            if(material.normalMapFilename != null) {
                glActiveTexture(GL_TEXTURE0 + unit);
                material.normalMap.bind(GL_TEXTURE_2D);
                shader.setUniform("uMaterial.normalMap", unit);
                shader.setUniform("uHasNormalMap", 1);
                unit++;
            } else {
                shader.setUniform("uHasNormalMap", 0);
            }
            
            if(material.metallicMapFilename != null) {
                glActiveTexture(GL_TEXTURE0 + unit);
                material.metallicMap.bind(GL_TEXTURE_2D);
                shader.setUniform("uMaterial.metallicMap", unit);
                shader.setUniform("uHasMetallicMap", 1);
                unit++;
            } else {
                shader.setUniform("uHasMetallicMap", 0);
            }
            
            if(material.roughnessMapFilename != null) {
                glActiveTexture(GL_TEXTURE0 + unit);
                material.roughnessMap.bind(GL_TEXTURE_2D);
                shader.setUniform("uMaterial.roughnessMap", unit);
                shader.setUniform("uHasRoughnessMap", 1);
                unit++;
            } else {
                shader.setUniform("uHasRoughnessMap", 0);
            }
            
            glBindVertexArray(mesh.vao);
            glDrawElements(GL_TRIANGLES, mesh.indices.length, GL_UNSIGNED_INT, 0);
        }
        
        glBindVertexArray(0);
        ErrorUtils.checkGLError();
    }
    
}