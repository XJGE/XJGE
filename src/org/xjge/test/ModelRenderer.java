package org.xjge.test;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
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
    private final Matrix4f identityMatrix = new Matrix4f().identity();
    private ModelAnimator animator;

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
        this.position = new Vector3f(x, y, z);
        bindVertexData();
        model.addReloadListener(this);
    }

    private void bindVertexData() {
        for (Mesh2 mesh : model.meshes) {
            int vertexCount = mesh.positions.length / 3;

            // Interleaved vertex data: pos(3), normal(3), uv(2), tangent(3), weights(4)
            FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertexCount * 15);

            // Separate bone ID buffer
            IntBuffer boneIDBuffer = MemoryUtil.memAllocInt(vertexCount * 4);

            for (int i = 0; i < vertexCount; i++) {
                int pi = i * 3;
                int ui = i * 2;
                int bi = i * 4;

                // Position
                vertexBuffer.put(mesh.positions[pi]).put(mesh.positions[pi + 1]).put(mesh.positions[pi + 2]);

                // Normal
                vertexBuffer.put(mesh.normals[pi]).put(mesh.normals[pi + 1]).put(mesh.normals[pi + 2]);

                // UV
                vertexBuffer.put(mesh.uvs[ui]).put(mesh.uvs[ui + 1]);

                // Tangent
                vertexBuffer.put(mesh.tangents[pi]).put(mesh.tangents[pi + 1]).put(mesh.tangents[pi + 2]);

                // Bone weights
                vertexBuffer.put(mesh.boneWeights[bi])
                            .put(mesh.boneWeights[bi + 1])
                            .put(mesh.boneWeights[bi + 2])
                            .put(mesh.boneWeights[bi + 3]);

                // Bone IDs
                boneIDBuffer.put(mesh.boneIDs[bi])
                            .put(mesh.boneIDs[bi + 1])
                            .put(mesh.boneIDs[bi + 2])
                            .put(mesh.boneIDs[bi + 3]);
            }
            vertexBuffer.flip();
            boneIDBuffer.flip();

            int vao = glGenVertexArrays();
            int vboFloats = glGenBuffers();
            int vboInts = glGenBuffers();
            int ebo = glGenBuffers();

            glBindVertexArray(vao);

            // --- Vertex Attributes ---
            glBindBuffer(GL_ARRAY_BUFFER, vboFloats);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            int stride = 15 * Float.BYTES; // 3+3+2+3+4

            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0); // position
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES); // normal
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES); // uv
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8 * Float.BYTES); // tangent
            glEnableVertexAttribArray(3);

            glVertexAttribPointer(5, 4, GL_FLOAT, false, stride, 11 * Float.BYTES); // weights
            glEnableVertexAttribArray(5);

            // --- Bone IDs ---
            glBindBuffer(GL_ARRAY_BUFFER, vboInts);
            glBufferData(GL_ARRAY_BUFFER, boneIDBuffer, GL_STATIC_DRAW);
            glVertexAttribIPointer(4, 4, GL_INT, 0, 0); // tightly packed iVec4
            glEnableVertexAttribArray(4);

            // --- Element buffer ---
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.indices, GL_STATIC_DRAW);

            glBindVertexArray(0);

            vaos.add(vao);
            indexCounts.add(mesh.indices.length);

            MemoryUtil.memFree(vertexBuffer);
            MemoryUtil.memFree(boneIDBuffer);
        }

        // Load textures
        for (Material2 material : model.materials) {
            if (material.albedoMapFilename != null)
                albedoMap = Texture.load(material.albedoMapFilename, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_SRGB_ALPHA, GL_LINEAR);
            if (material.normalMapFilename != null)
                normalMap = Texture.load(material.normalMapFilename, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
            if (material.roughnessMapFilename != null)
                roughnessMap = Texture.load(material.roughnessMapFilename, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
            if (material.metallicMapFilename != null)
                metallicMap = Texture.load(material.metallicMapFilename, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_RGBA, GL_LINEAR);
        }
    }

    void render(Camera camera) {
        modelMatrix.identity().translation(position);
        normalMatrix.set(modelMatrix).invert().transpose();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        shader.use();

        // --- Skinning ---
        if (animator != null) {
            Matrix4f[] animatedBones = animator.getSkinningMatrices();
            float[] boneData = new float[128 * 16]; // 128 mat4s, 16 floats each

            for (int i = 0; i < 128; i++) {
                Matrix4f m = (i < animatedBones.length && animatedBones[i] != null) ? animatedBones[i] : identityMatrix;
                m.get(boneData, i * 16); // column-major
            }

            shader.setUniform("uBones", false, boneData);
        }

        shader.setUniform("uModel", false, modelMatrix);
        shader.setUniform("uView", false, camera.getViewMatrix());
        shader.setUniform("uProjection", false, camera.getProjectionMatrix());
        shader.setUniform("uNormalMatrix", true, normalMatrix);
        shader.setUniform("uCamPos", camera.position);
        shader.setUniform("uNumLights", LightingSystem.getActiveCount());

        for (int i = 0; i < model.meshes.size(); i++) {
            Material2 material = model.materials.get(model.meshMaterialIndices.get(i));
            bindMaterial(material);

            glBindVertexArray(vaos.get(i));
            glDrawElements(GL_TRIANGLES, indexCounts.get(i), GL_UNSIGNED_INT, 0);
        }

        glBindVertexArray(0);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }

    public void setAnimator(ModelAnimator animator) {
        this.animator = animator;
    }

    @Override
    public void onAssetReload(Asset asset) {
        System.out.println("Change detected in model data");
        // TODO: re-upload vertex buffers if mesh changed
    }

    private void bindMaterial(Material2 material) {
        shader.setUniform("uMaterial.albedo", material.albedoColor);
        shader.setUniform("uMaterial.metallic", material.metallic);
        shader.setUniform("uMaterial.roughness", material.roughness);

        int unit = 0;
        if (material.albedoMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            albedoMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uAlbedoMap", unit);
            shader.setUniform("uHasAlbedoMap", 1);
            unit++;
        } else shader.setUniform("uHasAlbedoMap", 0);

        if (material.normalMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            normalMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uNormalMap", unit);
            shader.setUniform("uHasNormalMap", 1);
            unit++;
        } else shader.setUniform("uHasNormalMap", 0);

        if (material.roughnessMapFilename != null) {
            glActiveTexture(GL_TEXTURE0 + unit);
            roughnessMap.bind(GL_TEXTURE_2D);
            shader.setUniform("uMetallicRoughnessMap", unit);
            shader.setUniform("uHasMetallicRoughnessMap", 1);
        } else shader.setUniform("uHasMetallicRoughnessMap", 0);
    }
    
}