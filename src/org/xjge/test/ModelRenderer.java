package org.xjge.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.xjge.core.Asset;
import org.xjge.core.AssetReloadListener;
import org.xjge.core.Camera;
import org.xjge.core.EntityComponent;
import org.xjge.core.ErrorUtils;
import org.xjge.core.LightingSystem;
import org.xjge.graphics.Shader;
import org.xjge.graphics.ShaderStage;
import org.xjge.graphics.Texture;
import org.xjge.modeling.Material2;
import org.xjge.modeling.Mesh2;
import org.xjge.modeling.Model2;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class ModelRenderer extends EntityComponent implements AssetReloadListener {
    
    private final Model2 model;
    private final Vector3f position;
    private final Matrix3f normalMatrix = new Matrix3f();
    private final Matrix4f modelMatrix = new Matrix4f();
    
    private Texture albedoMap;
    private Texture normalMap;
    private Texture metallicMap;
    private Texture roughnessMap;
    
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> indexCounts = new ArrayList<>();
    
    private static final Shader shader;
    
    static {
        var shaderSourceFiles = new LinkedList<ShaderStage>() {{
            add(ShaderStage.load("shader_model_vertex.glsl", GL_VERTEX_SHADER));
            add(ShaderStage.load("shader_model_fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        shader = new Shader(shaderSourceFiles, "test_object");
    }
    
    ModelRenderer(Model2 model, float x, float y, float z) {
        this.model = model;
        
        position = new Vector3f(x, y, z);
        
        //TODO: hot reload on vertex data wont work until new values are uploaded to the GPU
        bindVertexData();
    }
    
    private void bindVertexData() {
        model.addReloadListener(this);
        
        for(Mesh2 mesh : model.meshes) {
            float[] interleaved = buildInterleaved(mesh);
            int stride = 11 * Float.BYTES;

            int vao = glGenVertexArrays();
            int vbo = glGenBuffers();
            int ebo = glGenBuffers();

            glBindVertexArray(vao);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, interleaved, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.indices, GL_STATIC_DRAW);

            //Position
            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);

            //Normal
            glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3L * Float.BYTES);
            glEnableVertexAttribArray(1);

            //UV
            glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6L * Float.BYTES);
            glEnableVertexAttribArray(2);

            //Tangent
            glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8L * Float.BYTES);
            glEnableVertexAttribArray(3);

            glBindVertexArray(0);

            vaos.add(vao);
            indexCounts.add(mesh.indices.length);
        }
        
        for(var material : model.materials) {
            if(material.albedoMapFilename != null) {
                albedoMap = Texture.load(material.albedoMapFilename, 
                        GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_SRGB_ALPHA, GL_LINEAR);
            }
            
            if(material.normalMapFilename != null) {
                normalMap = Texture.load(material.normalMapFilename, 
                        GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
            }
            
            if(material.roughnessMapFilename != null) {
                roughnessMap = Texture.load(material.roughnessMapFilename, 
                        GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
            }
            
            if(material.metallicMapFilename != null) {
                metallicMap = Texture.load(material.metallicMapFilename, 
                        GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
            }
        }
    }
    
    private float[] buildInterleaved(Mesh2 mesh) {
        int vertexCount = mesh.positions.length / 3;
        float[] data = new float[vertexCount * 11];

        for(int i = 0; i < vertexCount; i++) {
            int p = i * 3;
            int u = i * 2;
            int d = i * 11;
            
            //Position
            data[d]     = mesh.positions[p];
            data[d + 1] = mesh.positions[p + 1];
            data[d + 2] = mesh.positions[p + 2];

            //Normal
            data[d + 3] = mesh.normals != null ? mesh.normals[p]     : 0f;
            data[d + 4] = mesh.normals != null ? mesh.normals[p + 1] : 0f;
            data[d + 5] = mesh.normals != null ? mesh.normals[p + 2] : 1f;

            //TexCoords
            if(mesh.uvs != null) {
                data[d + 6] = mesh.uvs[u];
                data[d + 7] = mesh.uvs[u + 1];
            }

            //Tangent
            if(mesh.tangents != null) {
                data[d + 8]  = mesh.tangents[p];
                data[d + 9]  = mesh.tangents[p + 1];
                data[d + 10] = mesh.tangents[p + 2];
            }
        }

        return data;
    }
    
    private void bindMaterial(Material2 material) {
        ErrorUtils.checkGLError();
        
        shader.setUniform("uMaterial.albedo", material.albedoColor);
        shader.setUniform("uMaterial.metallic", material.metallic);
        shader.setUniform("uMaterial.roughness", material.roughness);

        int unit = 0;

        if(material.albedoMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            albedoMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uAlbedoMap", unit);
            shader.setUniform("uHasAlbedoMap", 1);
            unit++;
        } else shader.setUniform("uHasAlbedoMap", 0);
        
        ErrorUtils.checkGLError();

        if(material.normalMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            normalMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uNormalMap", unit);
            shader.setUniform("uHasNormalMap", 1);
            unit++;
        } else shader.setUniform("uHasNormalMap", 0);
        
        ErrorUtils.checkGLError();
        
        if(material.roughnessMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            roughnessMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uMetallicRoughnessMap", unit);
            shader.setUniform("uHasMetallicRoughnessMap", 1);
        } else shader.setUniform("uHasMetallicRoughnessMap", 0);
        
        ErrorUtils.checkGLError();
    }
    
    void render(Camera camera) {
        modelMatrix.identity().translation(position);
        normalMatrix.set(modelMatrix).invert().transpose();
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        shader.use();
        shader.setUniform("uModel", false, modelMatrix);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uNormalMatrix", true, normalMatrix);
        shader.setUniform("uCamPos", camera.position);
        shader.setUniform("uNumLights", LightingSystem.getActiveCount());
        
        for(int i = 0; i < model.meshes.size(); i++) {
            var material = model.materials.get(model.meshMaterialIndices.get(i));

            bindMaterial(material);

            glBindVertexArray(vaos.get(i));
            glDrawElements(GL_TRIANGLES, indexCounts.get(i), GL_UNSIGNED_INT, 0);
        }
        
        glBindVertexArray(0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void onAssetReload(Asset asset) {
        System.out.println("Change detected in model data");
        //TODO: bindVertexData(); //upload new data
    }
    
}