package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.LightSource;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.core.XJGE;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Jun 16, 2021
 */

public class Model {

    public static final int MAX_TEXTURES   = 4;
    public static final int MAX_BONES      = 128;
    public static final int MAX_ANIM_SPEED = 5;
    public static final int MAX_WEIGHTS    = 4;
    
    private int prevNumKeyFrames;
    
    private float speed = 1.5f;
    
    public boolean loopAnimation = true;
    
    public Color color = Color.WHITE;
    
    private AIScene aiScene;
    private Matrix4f rootTransform;
    private Node rootNode;
    private SkeletalAnimation currAnimation;
    private final Vector3f noValue = new Vector3f();
    private final Matrix3f normal  = new Matrix3f();
    
    private Mesh[] meshes;
    private Texture[] textures;
    
    private final ArrayList<Bone> bones = new ArrayList<>();
    
    private Map<String, SkeletalAnimation> animations;
    
    public Model(String filename, int aiArgs) {
        try(InputStream file = Model.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            loadModel(filename, file, aiArgs);
        } catch(Exception e) {
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to load model \"" + filename + "\"", e);
            Logger.setDomain(null);
        }
    }
    
    public Model(String filename) {
        this(filename, 
             aiProcess_JoinIdenticalVertices | 
             aiProcess_Triangulate | 
             aiProcess_GenSmoothNormals | 
             aiProcess_LimitBoneWeights | 
             aiProcess_FixInfacingNormals);
    }
    
    private void loadModel(String filename, InputStream file, int aiArgs) throws Exception {
        byte[] data = file.readAllBytes();
        
        ByteBuffer modelBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
        AIFileIO aiFileIO   = AIFileIO.create();
        AIFile aiFile       = AIFile.create();
        
        AIFileOpenProcI openProcedure = new AIFileOpenProc() {
            @Override
            public long invoke(long pFileIO, long fileName, long openMode) {
                AIFileReadProcI readProcedure = new AIFileReadProc() {
                    @Override
                    public long invoke(long pFile, long pBuffer, long size, long count) {
                        long numBytes = Math.min(modelBuf.remaining(), size * count);
                        MemoryUtil.memCopy(MemoryUtil.memAddress(modelBuf) + modelBuf.position(), pBuffer, numBytes);

                        return numBytes;
                    }
                };

                AIFileSeekI seekProcedure = new AIFileSeek() {
                    @Override
                    public int invoke(long pFile, long offset, int origin) {
                        switch(origin) {
                            case Assimp.aiOrigin_CUR -> modelBuf.position(modelBuf.position() + (int) offset);
                            case Assimp.aiOrigin_SET -> modelBuf.position((int) offset);
                            case Assimp.aiOrigin_END -> modelBuf.position(modelBuf.limit() + (int) offset);
                        }

                        return 0;
                    }
                };

                AIFileTellProcI tellProcedure = new AIFileTellProc() {
                    @Override
                    public long invoke(long pFile) { return modelBuf.limit(); }
                };

                aiFile.ReadProc(readProcedure);
                aiFile.SeekProc(seekProcedure);
                aiFile.FileSizeProc(tellProcedure);

                return aiFile.address();
            }
        };
        
        AIFileCloseProcI closeProcedure = new AIFileCloseProc() {
            @Override
            public void invoke(long pFileIO, long pFile) {}
        };
        
        aiFileIO.set(openProcedure, closeProcedure, NULL);
        
        aiScene = aiImportFileEx((XJGE.getAssetsFilepath() + filename), aiArgs, aiFileIO);
        
        if(aiScene == null) {
            MemoryUtil.memFree(modelBuf);
            throw new IllegalStateException(aiGetErrorString());
        } else {
            MemoryUtil.memFree(modelBuf);
            
            AINode aiRoot = aiScene.mRootNode();
            
            if(aiRoot != null) {
                rootTransform = Graphics.convertFromAssimp(aiRoot.mTransformation());
                rootNode      = parseFileHierarchy(aiRoot, null);
            } else {
                throw new NullPointerException("Could not locate root node in file hierarchy.");
            }
            
            parseMeshData(aiScene.mMeshes());
            parseTextureData(aiScene.mMaterials());
            parseAnimationData(aiScene.mAnimations());
        }
    }
    
    private Node parseFileHierarchy(AINode aiNode, Node parent) {
        String nodeName = aiNode.mName().dataString();
        Node node       = new Node(nodeName, parent);
        
        PointerBuffer childBuf = aiNode.mChildren();
        
        for(int i = 0; i < aiNode.mNumChildren(); i++) {
            AINode aiChild = AINode.create(childBuf.get(i));
            Node childNode = parseFileHierarchy(aiChild, node);
            
            node.children.add(childNode);
        }
        
        node.children = Collections.unmodifiableList(node.children);
        
        return node;
    }
    
    private void parseMeshData(PointerBuffer meshBuf) throws Exception {
        meshes = new Mesh[aiScene.mNumMeshes()];
        
        for(int i = 0; i < meshes.length; i++) {
            AIMesh aiMesh = AIMesh.create(meshBuf.get(i));
            meshes[i]     = new Mesh(aiMesh, bones);
        }
    }
    
    private void parseTextureData(PointerBuffer materialBuf) throws Exception {
        if(aiScene.mNumMaterials() > MAX_TEXTURES) {
            textures = new Texture[MAX_TEXTURES];
            Logger.setDomain("graphics");
            Logger.logWarning(
                    "Invalid number of textures. Limit of " + MAX_TEXTURES + 
                    " permitted, found " + aiScene.mNumMaterials(), 
                    null);
            Logger.setDomain(null);
        } else {
            textures = new Texture[aiScene.mNumMaterials()];
        }
        
        /*
        The reason we use materials to load model textures here is because we want to 
        load models from memory within the .jar file. As such, you must embed your 
        textures in whatever 3D modeling program you're using prior to loading it into 
        the engine.
        */
        
        for(int i = 0; i < textures.length; i++) {
            AIMaterial aiMaterial = AIMaterial.create(materialBuf.get(i));
            
            AIString filename = AIString.calloc();
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, filename, (IntBuffer) null, null, null, null, null, null);
            
            textures[i] = new Texture(filename.dataString());
            
            filename.free();
            
            glBindTexture(GL_TEXTURE_2D, textures[i].handle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    
    private void parseAnimationData(PointerBuffer animationBuf) {
        animations = new HashMap<>();
        
        for(int i = 0; i < aiScene.mNumAnimations(); i++) {
            AIAnimation aiAnimation  = AIAnimation.create(animationBuf.get(i));
            PointerBuffer channelBuf = aiAnimation.mChannels();
            
            for(int c = 0; c < aiAnimation.mNumChannels(); c++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(channelBuf.get(c));
                String nodeName       = aiNodeAnim.mNodeName().dataString();
                Node node             = rootNode.getNodeByName(nodeName);
                
                genTransforms(aiNodeAnim, node);
            }
            
            ArrayList<KeyFrame> keyFrames = genKeyFrames();
            prevNumKeyFrames              += keyFrames.size();
            
            SkeletalAnimation animation = new SkeletalAnimation(aiAnimation, keyFrames);
            animations.put(animation.name, animation);
        }
    }
    
    private void genTransforms(AINodeAnim aiNodeAnim, Node node) {
        AIVectorKey.Buffer aiPosKeyBuf   = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer aiScaleKeyBuf = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer aiRotKeyBuf     = aiNodeAnim.mRotationKeys();
        
        for(int i = 0; i < aiNodeAnim.mNumPositionKeys(); i++) {
            AIVectorKey aiVecKey = aiPosKeyBuf.get(i);
            AIVector3D aiVec     = aiVecKey.mValue();
            
            Matrix4f transform = new Matrix4f().translate(aiVec.x(), aiVec.y(), aiVec.z());
            
            AIQuatKey aiQuatKey    = aiRotKeyBuf.get(i);
            AIQuaternion aiQuat    = aiQuatKey.mValue();
            Quaternionf quaternion = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            
            transform.rotate(quaternion);
            
            if(i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = aiScaleKeyBuf.get(i);
                aiVec    = aiVecKey.mValue();
                
                transform.scale(aiVec.x(), aiVec.y(), aiVec.z());
            }
            
            node.transforms.add(transform);
        }
    }
    
    private ArrayList<KeyFrame> genKeyFrames() {
        ArrayList<KeyFrame> frames = new ArrayList<>();
        
        /*
        We provide the prevNumKeyFrames field here to offset the starting frame
        of each animation since the keyframes specified by the model file are
        stored sequentially regardless of animation.
        */
        
        for(int i = prevNumKeyFrames; i < rootNode.getNumKeyFrames(); i++) {
            KeyFrame frame = new KeyFrame();
            frames.add(frame);
            
            for(int b = 0; b < bones.size(); b++) {
                Bone bone = bones.get(b);
                Node node = rootNode.getNodeByName(bone.name);

                Matrix4f boneTransform = Node.getParentTransform(node, i);

                boneTransform.mul(bone.offset);
                boneTransform = new Matrix4f(rootTransform).mul(boneTransform);

                frame.transforms.get(b).set(boneTransform);
            }
        }
        
        return frames;
    }
    
    public void delocalizeNormal() {
        for(Mesh mesh : meshes) normal.set(mesh.modelMatrix.invert());
    }
    
    public void translate(Vector3f position) {
        for(Mesh mesh : meshes) mesh.modelMatrix.translation(position);
    }
    
    public void translate(float x, float y, float z) {
        for(Mesh mesh : meshes) mesh.modelMatrix.translation(x, y, z);
    }
    
    public void rotateX(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateX((float) Math.toRadians(angle));
    }
    
    public void rotateY(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateY((float) Math.toRadians(angle));
    }
    
    public void rotateZ(float angle) {
        for(Mesh mesh : meshes) mesh.modelMatrix.rotateZ((float) Math.toRadians(angle));
    }
    
    public void scale(float factor) {
        for(Mesh mesh : meshes) mesh.modelMatrix.scale(factor);
    }
    
    public void listAnimations() {
        Logger.setDomain("graphics");
        animations.forEach((name, anim) -> Logger.logInfo(name));
        Logger.setDomain(null);
    }
    
    public void setAnimation(String name, int numFrames) {
        if(!animations.containsKey(name)) {
            Logger.setDomain("graphics");
            Logger.logWarning(
                    "Failed to set animation: \"" + name + "\". Model contains " + 
                    "no such animation.",
                    null);
            Logger.setDomain(null);
            return;
        }
        
        if(currAnimation != null && numFrames > 1) {
            if(currAnimation.name.equals(name)) {
                Logger.setDomain("graphics");
                Logger.logInfo("Animation: \"" + name +"\" is already playing.");
                Logger.setDomain(null);
                return;
            }
            
            var frames = new ArrayList<KeyFrame>();
            
            for(int f = 1; f <= numFrames; f++) {
                KeyFrame frame = new KeyFrame();
                
                for(int b = 0; b < MAX_BONES; b++) {
                    animations.get(name).setFrameTime(currAnimation.getFrameTime());
                    animations.get(name).setSeekTime(currAnimation.getSeekTime());
                    
                    Matrix4f transforms = new Matrix4f();
                    
                    currAnimation.calcTransition(b).lerp(animations.get(name).calcTransition(b), f / ((float) numFrames), transforms);
                    
                    frame.transforms.get(b).set(transforms);
                }
                
                frames.add(frame);
            }
            
            currAnimation = new SkeletalAnimation(currAnimation.name, name, frames);
        } else {
            currAnimation = animations.get(name);
        }
    }
    
    public void setAnimationSpeed(float speed) {
        if(speed > 1)      speed = 1;
        else if(speed < 0) speed = 0;
        
        this.speed = speed * MAX_ANIM_SPEED;
    }
    
    public void updateAnimation() {
        if(currAnimation.transition && currAnimation.getFinished()) {
            currAnimation = animations.get(currAnimation.nextAnim);
        }
        
        currAnimation.genCurrFrame(speed, loopAnimation);
    }
    
    public void render(GLProgram glProgram, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        glProgram.use();
        
        for(Mesh mesh : meshes) {
            glBindTexture(GL_TEXTURE_2D, textures[mesh.texIndex].handle);
            glBindVertexArray(mesh.vao);
            
            glProgram.setUniform("uType", 5);
            glProgram.setUniform("uModel", false, mesh.modelMatrix);
            glProgram.setUniform("uNormal", true, normal);
            glProgram.setUniform("uNumLights", numLights);
            glProgram.setUniform("uColor", color.asVec3());
            
            for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
                if(lights[i] != null) {
                    if(lights[i].getEnabled()) {
                        glProgram.setUniform("uLights[" + i + "].brightness", lights[i].getBrightness());
                        glProgram.setUniform("uLights[" + i + "].contrast",   lights[i].getContrast());
                        glProgram.setUniform("uLights[" + i + "].position",   lights[i].getPosition());
                        glProgram.setUniform("uLights[" + i + "].ambient",    lights[i].getAmbientColor());
                        glProgram.setUniform("uLights[" + i + "].diffuse",    lights[i].getDiffuseColor());
                    } else {
                        glProgram.setUniform("uLights[" + i + "].brightness", 0);
                        glProgram.setUniform("uLights[" + i + "].contrast",   0);
                        glProgram.setUniform("uLights[" + i + "].position",   noValue);
                        glProgram.setUniform("uLights[" + i + "].ambient",    noValue);
                        glProgram.setUniform("uLights[" + i + "].diffuse",    noValue);
                    }
                }
            }
            
            if(currAnimation != null) {
                glProgram.setUniform("uBoneTransforms", false, currAnimation.getCurrFrame().transforms);
            }
            
            glDrawElements(GL_TRIANGLES, mesh.indices.capacity(), GL_UNSIGNED_INT, 0);
        }
        
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        
        ErrorUtils.checkGLError();
    }
    
    public void freeBuffers() {
        for(Mesh mesh : meshes) mesh.freeBuffers();
    }
    
    public void freeTextures() {
        for(Texture texture : textures) texture.freeTexture();
    }
    
}