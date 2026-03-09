package org.xjge.modeling;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class AssimpLoader {

    private static final int FLAGS = Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenSmoothNormals |
                                     Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_JoinIdenticalVertices |
                                     Assimp.aiProcess_LimitBoneWeights | Assimp.aiProcess_ImproveCacheLocality;
    
    static AssimpSceneData parse(InputStream stream) throws Exception {
        var data   = stream.readAllBytes();
        var buffer = MemoryUtil.memAlloc(data.length);
        
        buffer.put(data).flip();
        
        var aiScene = Assimp.aiImportFileFromMemory(buffer, FLAGS, "");
        if(aiScene == null) throw new RuntimeException(Assimp.aiGetErrorString());
        
        List<Mesh2> meshes                = parseMeshes(aiScene);
        List<Material2> materials         = parseMaterials(aiScene);
        List<Integer> meshMaterialIndices = parseMeshMaterialIndices(aiScene);
        Skeleton skeleton                 = parseSkeleton(aiScene);
        
        Assimp.aiReleaseImport(aiScene);
        MemoryUtil.memFree(buffer);
        
        return new AssimpSceneData(skeleton, meshes, materials, meshMaterialIndices);
    }
    
    static List<Mesh2> parseMeshes(AIScene aiScene) {
        List<Mesh2> meshes     = new ArrayList<>();
        PointerBuffer aiMeshes = aiScene.mMeshes();

        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh2 mesh = parseMesh(aiMesh);
            meshes.add(mesh);
        }

        return meshes;
    }
    
    static List<Material2> parseMaterials(AIScene aiScene) {
        List<Material2> materials  = new ArrayList<>();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        
        for(int i = 0; i < aiScene.mNumMaterials(); i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                Material2 material     = new Material2();
                
                material.albedoColor.set(parseVector3f(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE));
                material.metallic  = parseFloat(aiMaterial, Assimp.AI_MATKEY_METALLIC_FACTOR);
                material.roughness = parseFloat(aiMaterial, Assimp.AI_MATKEY_ROUGHNESS_FACTOR);
                material.albedoMapFilename    = parseTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE);
                material.normalMapFilename    = parseTextureFilename(aiMaterial, Assimp.aiTextureType_NORMALS);
                material.metallicMapFilename  = parseTextureFilename(aiMaterial, Assimp.aiTextureType_METALNESS);
                material.roughnessMapFilename = parseTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE_ROUGHNESS);
                
                materials.add(material);
        }
        
        return materials;
    }
    
    static List<Integer> parseMeshMaterialIndices(AIScene aiScene) {
        List<Integer> indices = new ArrayList<>();
        
        PointerBuffer meshes = aiScene.mMeshes();
        
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(meshes.get(i));
            indices.add(mesh.mMaterialIndex());
        }
        
        return indices;
    }
    
    static Skeleton parseSkeleton(AIScene aiScene) {
        Set<String> boneNames = collectBoneNames(aiScene);
        Skeleton skeleton     = new Skeleton();
        AINode rootNode       = aiScene.mRootNode();

        processNode(rootNode, -1, boneNames, skeleton);

        applyBoneOffsets(aiScene, skeleton);

        return skeleton;
    }
    
    private static Float parseFloat(AIMaterial aiMaterial, CharSequence key) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer value = stack.mallocFloat(1);
            IntBuffer max = stack.mallocInt(1);
            max.put(0, 1);

            int result = Assimp.aiGetMaterialFloatArray(aiMaterial, key, Assimp.aiTextureType_NONE, 0, value, max);
            if(result != Assimp.aiReturn_SUCCESS) value.put(0).flip();

            return value.get(0);
        }
    }
    
    private static Vector3f parseVector3f(AIMaterial aiMaterial, CharSequence key) {
        AIColor4D aiColor = AIColor4D.create();
        
        int result = Assimp.aiGetMaterialColor(aiMaterial, key, Assimp.aiTextureType_NONE, 0, aiColor);
        if(result != Assimp.aiReturn_SUCCESS) return null;
        
        return new Vector3f(aiColor.r(), aiColor.g(), aiColor.b());
    }
    
    private static String parseTextureFilename(AIMaterial aiMaterial, int type) {
        AIString filepath = AIString.calloc();
        
        int result = Assimp.aiGetMaterialTexture(aiMaterial, type, 0, filepath, (IntBuffer) null, null, null, null, null, null);
        if(result != Assimp.aiReturn_SUCCESS) return null;
        
        return filepath.dataString();
    }
    
    private static Mesh2 parseMesh(AIMesh aiMesh) {
        float[] positions = extractPositions(aiMesh);
        float[] normals   = extractNormals(aiMesh);
        float[] texCoords = extractUVs(aiMesh);
        float[] tangents  = extractTangents(aiMesh);
        int[] indices     = extractIndices(aiMesh);

        return new Mesh2(positions, normals, texCoords, tangents, indices);
    }
    
    private static float[] extractPositions(AIMesh aiMesh) {
        int vertexCount = aiMesh.mNumVertices();
        float[] positions = new float[vertexCount * 3];

        AIVector3D.Buffer vertices = aiMesh.mVertices();

        for(int i = 0; i < vertexCount; i++) {
            AIVector3D vertex = vertices.get(i);
            positions[i * 3]     = vertex.x();
            positions[i * 3 + 1] = vertex.y();
            positions[i * 3 + 2] = vertex.z();
        }

        return positions;
    }
    
    private static float[] extractNormals(AIMesh aiMesh) {
        if(aiMesh.mNormals() == null) return null;

        int vertexCount = aiMesh.mNumVertices();
        float[] normals = new float[vertexCount * 3];

        AIVector3D.Buffer aiNormals = aiMesh.mNormals();

        for(int i = 0; i < vertexCount; i++) {
            AIVector3D normal = aiNormals.get(i);
            normals[i * 3]     = normal.x();
            normals[i * 3 + 1] = normal.y();
            normals[i * 3 + 2] = normal.z();
        }

        return normals;
    }
    
    private static float[] extractUVs(AIMesh aiMesh) {
        AIVector3D.Buffer texCoords = aiMesh.mTextureCoords(0);
        if(texCoords == null) return null;

        int vertexCount = aiMesh.mNumVertices();
        float[] uvs = new float[vertexCount * 2];

        for(int i = 0; i < vertexCount; i++) {
            AIVector3D uv = texCoords.get(i);
            uvs[i * 2]     = uv.x();
            uvs[i * 2 + 1] = uv.y();
        }

        return uvs;
    }
    
    private static float[] extractTangents(AIMesh aiMesh) {
        if(aiMesh.mTangents() == null) return null;

        int vertexCount = aiMesh.mNumVertices();
        float[] tangents = new float[vertexCount * 3];

        AIVector3D.Buffer aiTangents = aiMesh.mTangents();

        for(int i = 0; i < vertexCount; i++) {
            AIVector3D tangent  = aiTangents.get(i);
            tangents[i * 3]     = tangent.x();
            tangents[i * 3 + 1] = tangent.y();
            tangents[i * 3 + 2] = tangent.z();
        }

        return tangents;
    }
    
    private static int[] extractIndices(AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        int[] indices = new int[faceCount * 3];

        AIFace.Buffer faces = aiMesh.mFaces();
        
        for(int i = 0; i < faceCount; i++) {
            AIFace face = faces.get(i);
            IntBuffer indexBuffer = face.mIndices();

            indices[i * 3]     = indexBuffer.get(0);
            indices[i * 3 + 1] = indexBuffer.get(1);
            indices[i * 3 + 2] = indexBuffer.get(2);
        }

        return indices;
    }
    
    private static Set<String> collectBoneNames(AIScene aiScene) {
        Set<String> boneNames = new HashSet<>();
        PointerBuffer meshes  = aiScene.mMeshes();

        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(meshes.get(i));
            
            PointerBuffer bones = mesh.mBones();
            if(bones == null) continue;

            for(int j = 0; j < mesh.mNumBones(); j++) {
                AIBone bone = AIBone.create(bones.get(j));
                boneNames.add(bone.mName().dataString());
            }
        }

        return boneNames;
    }
    
    private static void processNode(
        AINode node,
        int parentIndex,
        Set<String> boneNames,
        Skeleton skeleton) {
        
        String nodeName = node.mName().dataString();

        int boneIndex = parentIndex;

        if(boneNames.contains(nodeName)) {
            Bone2 bone = new Bone2();
            bone.name = nodeName;
            bone.parentIndex = parentIndex;

            convertMatrix(node.mTransformation(), bone.bindTransform);

            skeleton.bones.add(bone);

            boneIndex = skeleton.bones.size() - 1;

            skeleton.boneIndices.put(nodeName, boneIndex);

            if(parentIndex != -1)
                skeleton.bones.get(parentIndex).children.add(boneIndex);
            else
                skeleton.rootBoneIndex = boneIndex;
        }

        PointerBuffer children = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++) {

            processNode(
                AINode.create(children.get(i)),
                boneIndex,
                boneNames,
                skeleton
            );

        }
    }
    
    private static void applyBoneOffsets(AIScene aiScene, Skeleton skeleton) {
        PointerBuffer meshes = aiScene.mMeshes();

        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(meshes.get(i));

            PointerBuffer bones = mesh.mBones();
            if(bones == null) continue;

            for(int j = 0; j < mesh.mNumBones(); j++) {
                AIBone aiBone = AIBone.create(bones.get(j));
                String name   = aiBone.mName().dataString();
                Integer index = skeleton.boneIndices.get(name);
                
                if(index == null) continue;
                
                Bone2 bone = skeleton.bones.get(index);
                
                convertMatrix(aiBone.mOffsetMatrix(), bone.inverseBindTransform);
            }
        }
    }
    
    private static void convertMatrix(AIMatrix4x4 aiMatrix, Matrix4f dest) {
        dest.set(
            aiMatrix.a1(), aiMatrix.a2(), aiMatrix.a3(), aiMatrix.a4(),
            aiMatrix.b1(), aiMatrix.b2(), aiMatrix.b3(), aiMatrix.b4(),
            aiMatrix.c1(), aiMatrix.c2(), aiMatrix.c3(), aiMatrix.c4(),
            aiMatrix.d1(), aiMatrix.d2(), aiMatrix.d3(), aiMatrix.d4()
        );
    }
    
}