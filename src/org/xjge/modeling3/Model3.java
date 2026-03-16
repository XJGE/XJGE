package org.xjge.modeling3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
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
            animations = parseAnimations(aiScene);
            
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
    
    private List<Material3> parseMaterials(AIScene aiScene) {
        List<Material3> result    = new ArrayList<>();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        
        if(aiMaterials == null) return result;
        
        for(int i = 0; i < aiScene.mNumMaterials(); i++) {
            var aiMaterial = AIMaterial.create(aiMaterials.get(i));
            result.add(new Material3(aiMaterial));
        }
        
        return result;
    }
    
    private List<Mesh3> parseMeshes(AIScene aiScene) {
        List<Mesh3> result     = new ArrayList<>();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        
        if(aiMeshes == null) return result;
        
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            var aiMesh = AIMesh.create(aiMeshes.get(i));
            result.add(new Mesh3(aiMesh, skeleton));
        }
        
        return result;
    }
    
    private List<SkeletalAnimation3> parseAnimations(AIScene aiScene) {
        List<SkeletalAnimation3> result = new ArrayList<>();
        PointerBuffer aiAnimations      = aiScene.mAnimations();
        
        if(aiAnimations == null) return result;
        
        for(int i = 0; i < aiScene.mNumAnimations(); i++) {
            var aiAnimation = AIAnimation.create(aiAnimations.get(i));
            result.add(new SkeletalAnimation3(aiAnimation, skeleton));
        }
        
        return result;
    }
    
    static Matrix4f convertMatrix(AIMatrix4x4 aiMatrix) {
        return new Matrix4f().set(
            aiMatrix.a1(), aiMatrix.b1(), aiMatrix.c1(), aiMatrix.d1(),
            aiMatrix.a2(), aiMatrix.b2(), aiMatrix.c2(), aiMatrix.d2(),
            aiMatrix.a3(), aiMatrix.b3(), aiMatrix.c3(), aiMatrix.d3(),
            aiMatrix.a4(), aiMatrix.b4(), aiMatrix.c4(), aiMatrix.d4()
        );
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