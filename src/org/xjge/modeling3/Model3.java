package org.xjge.modeling3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Asset;
import org.xjge.core.AssetManager;
import org.xjge.core.Logger;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Model3 extends Asset {

    private final int flags;
    
    private List<Mesh3> meshes;
    private List<Material3> materials;
    private List<SkeletalAnimation3> animations;
    
    private enum VertexAttribute {
        
        POSITIONS(3),
        NORMALS(3),
        TANGENTS(3),
        UVS(2);
        
        final int stride;
        
        VertexAttribute(int stride) { this.stride = stride; }
        
    }
    
    public static final Model3 load(String filename) {
        var defaultFlags = Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenSmoothNormals |
                           Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_JoinIdenticalVertices |
                           Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_ImproveCacheLocality;
        
        return AssetManager.load(filename, () -> new Model3(filename, defaultFlags));
    }
    
    public static final Model3 load(String filename, int flags) {
        return AssetManager.load(filename, () -> new Model3(filename, flags));
    }
    
    private Model3(String filename, int flags) {
        super(filename);
        this.flags = flags;
    }
    
    @Override
    protected void onLoad(InputStream file) {
        try {
            var data   = file.readAllBytes();
            var buffer = MemoryUtil.memAlloc(data.length);
            
            buffer.put(data).flip();
            
            var aiScene = Assimp.aiImportFileFromMemory(buffer, flags, "");
            if(aiScene == null) throw new RuntimeException(Assimp.aiGetErrorString());
            
            AINode aiRootNode = aiScene.mRootNode();
            var rootTransform = convertMatrix(aiRootNode.mTransformation());
            
            meshes    = parseMeshes(aiScene);
            materials = parseMaterials(aiScene);
            
        } catch(IOException exception) {
            Logger.logWarning("Failed to load model: \"" + getFilename() + "\"", exception);
        }
    }

    @Override
    protected void onRelease() {
    }

    @Override
    protected <T extends Asset> T onLoadFailure() {
        return null;
    }
    
    private float extractMaterialValue(AIMaterial aiMaterial, CharSequence aiKey) {
        try(var stack = MemoryStack.stackPush()) {
            var value = stack.mallocFloat(1);
            var max   = stack.mallocInt(1);
            max.put(0, 1);

            int result = Assimp.aiGetMaterialFloatArray(aiMaterial, aiKey, Assimp.aiTextureType_NONE, 0, value, max);
            if(result != Assimp.aiReturn_SUCCESS) value.put(0).flip();

            return value.get(0);
        }
    }
    
    private float[] extractVertexAttribute(AIMesh aiMesh, VertexAttribute attribute) {
        AIVector3D.Buffer buffer;

        switch(attribute) {
            case POSITIONS -> buffer = aiMesh.mVertices();
            case NORMALS   -> buffer = aiMesh.mNormals();
            case TANGENTS  -> buffer = aiMesh.mTangents();
            case UVS       -> buffer = aiMesh.mTextureCoords(0);
            //TODO: extract bone weights
            default        -> throw new IllegalArgumentException("Unsupported vertex attribute found: \"" + attribute + "\"");
        }

        if(buffer == null) return null;

        int vertexCount = aiMesh.mNumVertices();
        float[] result  = new float[vertexCount * attribute.stride];

        for(int i = 0; i < vertexCount; i++) {
            var vec = buffer.get(i);

            result[i * attribute.stride]     = vec.x();
            result[i * attribute.stride + 1] = vec.y();

            if(attribute.stride == 3) result[i * attribute.stride + 2] = vec.z();
        }

        return result;
    }
    
    private int[] extractIndices(AIMesh aiMesh) {
        int faceCount       = aiMesh.mNumFaces();
        int[] indices       = new int[faceCount * 3];
        AIFace.Buffer faces = aiMesh.mFaces();
        
        for(int i = 0; i < faceCount; i++) {
            AIFace aiFace      = faces.get(i);
            IntBuffer idx      = aiFace.mIndices();
            indices[i * 3]     = idx.get(0);
            indices[i * 3 + 1] = idx.get(1);
            indices[i * 3 + 2] = idx.get(2);
        }

        return indices;
    }
    
    private String extractTextureFilename(AIMaterial aiMaterial, int textureType) {
        AIString filepath = AIString.calloc();
        
        int result = Assimp.aiGetMaterialTexture(aiMaterial, textureType, 0, filepath, (IntBuffer) null, null, null, null, null, null);
        if(result != Assimp.aiReturn_SUCCESS) return null;
        
        return filepath.dataString();
    }
    
    private Vector3f extractAlbedoColor(AIMaterial aiMaterial, CharSequence aiKey) {
        var aiColor = AIColor4D.create();
        
        int result = Assimp.aiGetMaterialColor(aiMaterial, aiKey, Assimp.aiTextureType_NONE, 0, aiColor);
        if(result != Assimp.aiReturn_SUCCESS) return new Vector3f(1f);
        
        return new Vector3f(aiColor.r(), aiColor.g(), aiColor.b());
    }
    
    private Matrix4f convertMatrix(AIMatrix4x4 aiMatrix) {
        return new Matrix4f().set(
            aiMatrix.a1(), aiMatrix.b1(), aiMatrix.c1(), aiMatrix.d1(),
            aiMatrix.a2(), aiMatrix.b2(), aiMatrix.c2(), aiMatrix.d2(),
            aiMatrix.a3(), aiMatrix.b3(), aiMatrix.c3(), aiMatrix.d3(),
            aiMatrix.a4(), aiMatrix.b4(), aiMatrix.c4(), aiMatrix.d4()
        );
    }
    
    private List<Mesh3> parseMeshes(AIScene aiScene) {
        List<Mesh3> result     = new ArrayList<>();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            var aiMesh = AIMesh.create(aiMeshes.get(i));
            var mesh   = new Mesh3();
            
            mesh.positions = extractVertexAttribute(aiMesh, VertexAttribute.POSITIONS);
            mesh.normals   = extractVertexAttribute(aiMesh, VertexAttribute.NORMALS);
            mesh.tangents  = extractVertexAttribute(aiMesh, VertexAttribute.TANGENTS);
            mesh.uvs       = extractVertexAttribute(aiMesh, VertexAttribute.UVS);
            
            result.add(mesh);
        }
        
        return result;
    }
    
    private List<Material3> parseMaterials(AIScene aiScene) {
        List<Material3> result    = new ArrayList<>();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        
        for(int i = 0; i < aiScene.mNumMaterials(); i++) {
            var aiMaterial = AIMaterial.create(aiMaterials.get(i));
            var material   = new Material3();
            
            material.metallic  = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_METALLIC_FACTOR);
            material.roughness = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_ROUGHNESS_FACTOR);
            material.opacity   = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_OPACITY);
            material.albedoColor.set(extractAlbedoColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE));
            material.albedoMapFilename    = extractTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE);
            material.normalMapFilename    = extractTextureFilename(aiMaterial, Assimp.aiTextureType_NORMALS);
            material.metallicMapFilename  = extractTextureFilename(aiMaterial, Assimp.aiTextureType_METALNESS);
            material.roughnessMapFilename = extractTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE_ROUGHNESS);
            
            result.add(material);
        }
        
        return result;
    }
    
    public final List<Material3> getMaterials() {
        return materials;
    }

}