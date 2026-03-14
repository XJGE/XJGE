package org.xjge.modeling3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
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

    public static final int MAX_BONES_PER_VERTEX = 4;
    
    private final int flags;
    
    private Matrix4f rootTransform;
    private final Skeleton3 skeleton = new Skeleton3();
    
    private List<Material3> materials;
    private List<Mesh3> meshes;
    private List<SkeletalAnimation3> animations;
    
    private enum VertexAttribute {
        
        POSITION(3),
        NORMAL(3),
        TANGENT(3),
        UV(2);
        
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
            rootTransform = convertMatrix(aiRootNode.mTransformation());
            
            materials = parseMaterials(aiScene);
            meshes    = parseMeshes(aiScene);
            buildBoneHierarchy(aiRootNode, -1);
            
            Assimp.aiReleaseImport(aiScene);
            MemoryUtil.memFree(buffer);
            
        } catch(IOException exception) {
            Logger.logWarning("Failed to load model: \"" + getFilename() + "\"", exception);
        }
    }

    @Override
    protected void onRelease() {
        rootTransform = null;
        materials.clear();
        meshes.clear();
        animations.clear();
        skeleton.bones.clear();
        skeleton.boneMap.clear();
    }

    @Override
    protected <T extends Asset> T onLoadFailure() {
        return null;
    }
    
    private void buildBoneHierarchy(AINode node, int parentIndex) {
        var nodeName  = node.mName().dataString();
        var boneIndex = skeleton.boneMap.get(nodeName);

        if(boneIndex != null) {
            skeleton.bones.get(boneIndex).parentIndex = parentIndex;
            parentIndex = boneIndex;
        }

        PointerBuffer children = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++) {
            buildBoneHierarchy(AINode.create(children.get(i)), parentIndex);
        }
    }
    
    private void extractBoneWeights(AIMesh aiMesh, Mesh3 mesh) {
        int vertexCount  = aiMesh.mNumVertices();
        mesh.boneIDs     = new int[vertexCount * MAX_BONES_PER_VERTEX];
        mesh.boneWeights = new float[vertexCount * MAX_BONES_PER_VERTEX];
        var aiBones      = aiMesh.mBones();
        int boneCount    = aiMesh.mNumBones();
        
        for(int boneIndex = 0; boneIndex < boneCount; boneIndex++) {
            var aiBone   = AIBone.create(aiBones.get(boneIndex));
            var weights  = aiBone.mWeights();
            var boneName = aiBone.mName().dataString();
            
            int pipe = boneName.indexOf("|");
            if(pipe != -1) boneName = boneName.substring(pipe + 1);
            
            var mapIndex = skeleton.boneMap.get(boneName);
            
            //Build resolver list, this helps us convert local mesh space to global space during rendering
            if(mapIndex == null) {
                mapIndex          = skeleton.bones.size();
                var bone          = new Bone3();
                bone.name         = boneName;
                bone.offsetMatrix = convertMatrix(aiBone.mOffsetMatrix());
                skeleton.bones.add(bone);
                skeleton.boneMap.put(boneName, mapIndex);
            }
            
            for(int w = 0; w < aiBone.mNumWeights(); w++) {
                var vw       = weights.get(w);
                int vertexID = vw.mVertexId();
                float weight = vw.mWeight();
                int base     = vertexID * MAX_BONES_PER_VERTEX;
                
                for(int i = 0; i < MAX_BONES_PER_VERTEX; i++) {
                    if(mesh.boneWeights[base + i] == 0f) {
                        mesh.boneIDs[base + i]     = mapIndex;
                        mesh.boneWeights[base + i] = weight;
                        break;
                    }
                }
            }
        }
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
            case POSITION -> buffer = aiMesh.mVertices();
            case NORMAL   -> buffer = aiMesh.mNormals();
            case TANGENT  -> buffer = aiMesh.mTangents();
            case UV       -> buffer = aiMesh.mTextureCoords(0);
            default       -> throw new IllegalArgumentException("Unsupported vertex attribute found: \"" + attribute + "\"");
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
        int faceCount = aiMesh.mNumFaces();
        int[] indices = new int[faceCount * 3];
        var faces     = aiMesh.mFaces();
        
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
        var filepath = AIString.calloc();
        
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
    
    private List<Mesh3> parseMeshes(AIScene aiScene) {
        List<Mesh3> result     = new ArrayList<>();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            var aiMesh = AIMesh.create(aiMeshes.get(i));
            var mesh   = new Mesh3();
            
            mesh.positions = extractVertexAttribute(aiMesh, VertexAttribute.POSITION);
            mesh.normals   = extractVertexAttribute(aiMesh, VertexAttribute.NORMAL);
            mesh.tangents  = extractVertexAttribute(aiMesh, VertexAttribute.TANGENT);
            mesh.uvs       = extractVertexAttribute(aiMesh, VertexAttribute.UV);
            extractBoneWeights(aiMesh, mesh);
            mesh.materialIndex = aiMesh.mMaterialIndex();
            mesh.indices       = extractIndices(aiMesh);
            
            result.add(mesh);
        }
        
        return result;
    }
    
    public final Skeleton3 getSkeleton() {
        return skeleton;
    }
    
    public final List<Material3> getMaterials() {
        return materials;
    }
    
    public final List<Mesh3> getMeshes() {
        return meshes;
    }
    
    public final List<SkeletalAnimation3> getAnimations() {
        return animations;
    }

}