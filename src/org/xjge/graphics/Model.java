package org.xjge.graphics;

import org.xjge.core.ErrorUtils;
import org.xjge.core.LightSource;
import org.xjge.core.Logger;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.xjge.core.Camera;

//Created: Jun 16, 2021

/**
 * Represents a 3D model. Models may possess animations, multiple meshes, 
 * textures, or a combination of these things. Various limitations are imposed 
 * by the engine to ensure models are loaded with consistency. The Autodesk 
 * .fbx file format is the preferred format of this engine for its compact 
 * size, though other formats should work as well.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Model {

    public static final int MAX_TEXTURES   = 8;
    public static final int MAX_BONES      = 128;
    public static final int MAX_ANIM_SPEED = 5;
    public static final int MAX_WEIGHTS    = 4;
    
    private int prevNumKeyFrames;
    
    private float speed = 1.5f;
    
    public boolean loopAnimation = true;
    
    public Color color = Color.WHITE;
    
    private final String filename;
    private AIScene aiScene;
    private Matrix4f rootTransform;
    private Node rootNode;
    private SkeletalAnimation currAnimation;
    private final Vector3f noValue        = new Vector3f();
    private final Matrix3f normal         = new Matrix3f();
    private final DefaultCaps defaultCaps = new DefaultCaps();
    
    public List<Mesh> meshes = new ArrayList<>();
    
    private Texture[] textures;
    private final ArrayList<Bone> bones = new ArrayList<>();
    private Map<String, SkeletalAnimation> animations;
    
    private class DefaultCaps extends GLCapabilities {
        @Override
        public void enable() {
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
        }

        @Override
        public void disable() {
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
        }
    }
    
    /**
     * Overloaded version of {@link Model(String)} that permits the use of 
     * custom post processing arguments.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     * @param aiArgs   the Assimp arguments to use for post processing such as 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_Triangulate 
     *                 aiProcess_Triangulate}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_GenSmoothNormals 
     *                 aiProcess_GenSmoothNormals}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_FixInfacingNormals 
     *                 aiProcess_FixInfacingNormals}, etc.
     */
    public Model(String filename, int aiArgs) {
        this.filename = filename;
        
        try(InputStream file = Model.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            loadModel(file, aiArgs);
        } catch(Exception e) {
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to load model \"" + filename + "\"", e);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Parses the file provided and generates a 3D model from the data it 
     * contains.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    public Model(String filename) {
        this(filename, 
             aiProcess_JoinIdenticalVertices | 
             aiProcess_Triangulate | 
             aiProcess_LimitBoneWeights | 
             aiProcess_FixInfacingNormals);
    }
    
    /**
     * Specifies various file open/read/close procedures and then constructs a 
     * new model instance using the data parsed from the file.
     * 
     * @param file     the file to extract model data from
     * @param aiArgs   the Assimp arguments to use for post processing such as 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_Triangulate 
     *                 aiProcess_Triangulate}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_GenSmoothNormals 
     *                 aiProcess_GenSmoothNormals}, 
     *                 {@link org.lwjgl.assimp.Assimp#aiProcess_FixInfacingNormals 
     *                 aiProcess_FixInfacingNormals}, etc.
     */
    private void loadModel(InputStream file, int aiArgs) throws Exception {
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
    
    /**
     * Translates the structure of the model file into a hierarchy that can be 
     * used by the engine.
     * 
     * @param aiNode the Assimp data structure from which a new node object 
     *               will be constructed
     * @param parent the parent of the unprocessed Assimp node or null if this 
     *               is the root node
     * 
     * @return a new child node of the parent provided 
     */
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
    
    /**
     * Parses mesh data used by this model.
     * 
     * @param meshBuf    the buffer of model mesh data provided by Assimp
     * 
     * @throws Exception if the data parsed from the file is invalid
     */
    private void parseMeshData(PointerBuffer meshBuf) throws Exception {
        for(int i = 0; i < aiScene.mNumMeshes(); i++) {
            AIMesh aiMesh = AIMesh.create(meshBuf.get(i));
            meshes.add(new Mesh(aiMesh, bones));
        }
        
        meshes = Collections.unmodifiableList(meshes);
    }
    
    /**
     * Parses each texture that will be used by this model.
     * <p>
     * The engine imposes a number of significant restrictions regarding 
     * textures that should be considered during the model creation process. 
     * Specifically;
     * <ol>
     * <li>Models may not exceed the maximum number of allowed textures 
     *     specified through the {@link MAX_TEXTURES} field. By default this 
     *     number is eight.</li>
     * <li>Any {@link Mesh} object representing part of this model can not use 
     *     more than one texture concurrently. That is, a single texture may be 
     *     shared between multiple meshes, but a single mesh may not exhibit 
     *     multiple textures.</li>
     * <li>Since models are loaded directly from memory, they must embed their 
     *     textures inside of materials in order to load correctly.</li>
     * <li>Texture image files used by models must be located in the same 
     *     directory as the model file itself.</li>
     * </ol>
     * 
     * @param materialBuf the buffer of models material data provided by Assimp
     * 
     * @throws Exception if one or more textures could not be located. The 
     *                   engine will instead use a placeholder texture.
     */
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
            
            AIString aiFilename = AIString.calloc();
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiFilename, (IntBuffer) null, null, null, null, null, null);
            
            textures[i] = new Texture(aiFilename.dataString());
            
            aiFilename.free();
            
            glBindTexture(GL_TEXTURE_2D, textures[i].handle);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
    
    /**
     * Parses data required by this model during skeletal animation.
     * 
     * @param animationBuf the buffer of the models animation data as provided 
     *                     by Assimp
     */
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
    
    /**
     * Generates the final transforms of each {@link Node} that will be used to move the bones of the model during a 
     * {@link SkeletalAnimation}.
     * 
     * @param aiNodeAnim the Assimp structure that will be parsed to calculate a nodes transformations
     * @param node       the node that will contain the calculated transformations
     */
    private void genTransforms(AINodeAnim aiNodeAnim, Node node) {
        AIVectorKey.Buffer aiPosKeyBuf   = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer aiScaleKeyBuf = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer aiRotKeyBuf     = aiNodeAnim.mRotationKeys();
        
        for(int i = 0; i < aiNodeAnim.mNumPositionKeys(); i++) {
            AIVectorKey aiVecKey = aiPosKeyBuf.get(i);
            AIVector3D aiVec     = aiVecKey.mValue();
            
            Matrix4f transform = new Matrix4f().translation(aiVec.x(), aiVec.y(), aiVec.z());
            
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
    
    /**
     * generates every {@link KeyFrame} of a {@link SkeletalAnimation}.
     * 
     * @return the list of keyframes used by the animation.
     */
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
    
    /**
     * Sets the texture of the specified mesh to the current 
     * {@code GL_TEXTURE_2D} target. This method is useful when you want to 
     * change various texture parameters to better suit the model.
     * <p>
     * NOTE: By default textures are loaded by the model class using 
     * mirrored-repeat wrapping and a nearest neighbor filter.
     * 
     * @param mesh the mesh who's texture we want to set as the current bind 
     *              target
     */
    public void bindMeshTexture(Mesh mesh) {
        if(meshes.contains(mesh)) {
            glBindTexture(GL_TEXTURE_2D, textures[mesh.textureID].handle);
        } else {
            String meshName = (mesh == null) ? "null" : mesh.name;
            
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to bind the texture of mesh \"" + meshName + 
                              "\" no such mesh is present in this model.", null);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Alternate version of {@link bindMeshTexture bindMeshTexture()}.
     * <p>
     * NOTE: Mesh names are not guaranteed to be unique. As such, you should 
     * only use this method if you're certain the name specified matches that of
     * the mesh who's texture you're trying to bind.
     * 
     * @param name the name that corresponds to the mesh we want to set as the 
     *              bind target
     */
    public void bindMeshTexture(String name) {
        meshes.forEach(mesh -> {
            if(mesh.name.equals(name)) {
                glBindTexture(GL_TEXTURE_2D, textures[mesh.textureID].handle);
            } 
        });
    }
    
    /**
     * Couples the local space of the models mesh normals to that of the 
     * current scenes world space. Use this to fix the direction of the light 
     * source relative to the model whenever it's being illuminated 
     * incorrectly.
     */
    public void delocalizeNormal() {
        meshes.forEach(mesh -> normal.set(mesh.modelMatrix.invert()));
    }
    
    /**
     * Translates the entire 3D model to the location specified.
     * 
     * @param position the position to set the model to
     */
    public void translation(Vector3f position) {
        meshes.forEach(mesh -> mesh.modelMatrix.translation(position));
    }
    
    /**
     * Alternate version of {@link translation(Vector3f) translation()}.
     * 
     * @param x the x-coordinate of the game world to place this model at
     * @param y the y-coordinate of the game world to place this model at
     * @param z the z-coordinate of the game world to place this model at
     */
    public void translation(float x, float y, float z) {
        meshes.forEach(mesh -> mesh.modelMatrix.translation(x, y, z));
    }
    
    /**
     * Changes the rotation of the entire 3D model relative to each axis. This 
     * method is essentially a combined version of {@link rotateX rotateX()}, 
     * {@link rotateY rotateY()}, and {@link rotateZ rotateZ()}.
     * 
     * @param angleX the angle to rotate the model along the x-axis
     * @param angleY the angle to rotate the model along the y-axis
     * @param angleZ the angle to rotate the model along the z-axis
     */
    public void rotationXYZ(float angleX, float angleY, float angleZ) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotationXYZ(angleX, angleY, angleZ));
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds x-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateX(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateX((float) Math.toRadians(angle)));
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds y-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateY(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateY((float) Math.toRadians(angle)));
    }
    
    /**
     * Rotates the entire 3D model in relation to the worlds z-axis.
     * 
     * @param angle the angle with which the model will be rotated
     */
    public void rotateZ(float angle) {
        meshes.forEach(mesh -> mesh.modelMatrix.rotateZ((float) Math.toRadians(angle)));
    }
    
    /**
     * Scales the entire 3D model by the factor specified.
     * 
     * @param factor the factor with which the models size will be multiplied 
     *               by
     */
    public void scale(float factor) {
        meshes.forEach(mesh -> mesh.modelMatrix.scale(factor));
    }
    
    /**
     * Outputs a list to the console containing information about every 
     * animation baked into this model.
     */
    public void listAnimations() {
        Logger.setDomain("graphics");        
        Logger.logInfo("Skeletal Animations for Model \"" + filename + "\":");
        animations.forEach((name, anim) -> Logger.logInfo("\"" + name + "\" (" + anim.duration + " sec)"));
        Logger.setDomain(null);
    }
    
    /**
     * Obtains the total duration of the specified animation as it was parsed 
     * from the model file.
     * 
     * @param name the name of the animation to query
     * 
     * @return the duration (in seconds) of the animation 
     */
    public float getAnimationDuration(String name) {
        return animations.get(name).duration;
    }
    
    /**
     * Checks whether the specified animation has finished playing.
     * 
     * @param name the name of the animation to query
     * 
     * @return true if the animation has completed a full cycle of all its 
     *         keyframes
     */
    public boolean getAnimationFinished(String name) {
        return animations.get(name).getFinished();
    }
    
    /**
     * Obtains a value which indicates how close we are to completing the 
     * specified animations current {@link KeyFrame}.
     * 
     * @param name the name of the animation to query
     * 
     * @return a non-negative number (between 0 and 1) indicating the 
     *         progression of the animations current keyframe
     */
    public float getAnimationFrameTime(String name) {
        return animations.get(name).getFrameTime();
    }
    
    /**
     * Obtains the number of keyframes the specified animation has.
     * 
     * @param name the name of the animation to query
     * 
     * @return the number of keyframes parsed from the file
     */
    public int getAnimationFrameCount(String name) {
        return animations.get(name).getFrameCount();
    }
    
    /**
     * Obtains the current amount of time that's elapsed in the specified 
     * animation.
     * 
     * @param name the name of the animation to query
     * 
     * @return the seek time of the animation (in seconds)
     */
    public float getAnimationSeekTime(String name) {
        return animations.get(name).getSeekTime();
    }
    
    /**
     * Sets the current animation that will be played by this model. A small 
     * transition animation will be generated if the value passed to the 
     * {@code numFrames} argument is greater than zero.
     * 
     * @param name      the name of the animation as it appears in the model 
     *                  file
     * @param numFrames the number of frames to transition between the current 
     *                  animation and the new one
     */
    public void setCurrentAnimation(String name, int numFrames) {
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
    
    /**
     * Sets the playback speed of this models current animation. Subsequent 
     * animations will inherit the value specified.
     * <p>
     * NOTE: The total number of keyframes in an animation should be evenly 
     * divisible otherwise you'll get some stuttering with values below 1. 
     * 
     * @param speed a non-negative number between 1 and 0. A value of zero will 
     *              pause the animation at its current {@link KeyFrame}.
     */
    public void setAnimationSpeed(float speed) {
        if(speed > 1)      speed = 1;
        else if(speed < 0) speed = 0;
        
        this.speed = speed * MAX_ANIM_SPEED;
    }
    
    /**
     * Sets the time elapsed between the current {@link KeyFrame} and the next 
     * one in sequence.
     * 
     * @param frameTime a non-negative number (between 0 and 1) indicating the 
     *                  progression of the animations current keyframe
     */
    public void setAnimationFrameTime(float frameTime) {
        currAnimation.setFrameTime(frameTime);
    }
    
    /**
     * Sets the current seek time of the animation. Similar in function to a 
     * slider on a video or movie that can be moved back and forth to change 
     * the current time/frame/scene.
     * 
     * @param seekTime a value to offset the animations current seek/elapsed 
     *                 time with
     */
    public void setAnimationSeekTime(float seekTime) {
        currAnimation.setSeekTime(seekTime);
    }
    
    /**
     * Steps the seek time of the models current animation forwards. A call to 
     * this method must be made regularly inside the update loop for models to 
     * utilize the engines animation utilities properly.
     * <p>
     * NOTE: the value passed to the delta parameter should be one of 
     * {@code targetDelta} or {@code trueDelta} provided by the 
     * {@linkplain org.xjge.core.Scene#update(double, double) update()} method 
     * of the Scene class.
     * 
     * @param delta the time (in seconds) it took to complete a single game 
     *              tick
     */
    public void updateAnimation(double delta) {
        if(currAnimation != null) {
            if(currAnimation.transition && currAnimation.getFinished()) {
                currAnimation = animations.get(currAnimation.nextAnim);
            }

            currAnimation.genCurrFrame(speed, loopAnimation, delta);
        }
    }
    
    /**
     * Renders the 3D model. Should be called from within the implementing 
     * entities {@link org.xjge.core.Entity#render(GLProgram, Camera, LightSource[], int) render()} method.
     * 
     * @param glProgram the shader program that will be used to render this 
     *                  model
     * @param lights    an array of light source objects inhabiting the current 
     *                  scene
     * @param numLights the total number of lights in the scene
     * @param capabilities an object that can be used to enable various OpenGL capabilities
     */
    public void render(GLProgram glProgram, LightSource[] lights, int numLights, GLCapabilities capabilities) {
        if(capabilities != null) capabilities.enable();
        
        glProgram.use();
        
        meshes.forEach(mesh -> {
            //TODO: look into why shadows arent displayed on textured objects.
            //glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textures[mesh.textureID].handle);
            
            /*
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer result = stack.callocInt(1);
                
                glGetIntegerv(GL_TEXTURE_BINDING_2D, result);
                
                System.out.println(result.get());
            }
            */
            
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
        });
        
        if(capabilities != null) capabilities.disable();
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Alternate version of {@link render render()} that uses the default 
     * {@link GLCapabilities} provided by the engine which includes backface 
     * culling and depth testing.
     * 
     * @param glProgram the shader program that will be used to render this 
     *                  model
     * @param lights    an array of light source objects inhabiting the current 
     *                  scene
     * @param numLights the total number of lights in the scene
     */
    public void render(GLProgram glProgram, LightSource[] lights, int numLights) {
        render(glProgram, lights, numLights, defaultCaps);
    }
    
    public void renderShadow(GLProgram depthProgram) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        
        meshes.forEach(mesh -> {
            glBindVertexArray(mesh.vao);
            depthProgram.setUniform("uModel", false, mesh.modelMatrix);
            glDrawElements(GL_TRIANGLES, mesh.indices.capacity(), GL_UNSIGNED_INT, 0);
        });
        
        glCullFace(GL_BACK);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        meshes.forEach(mesh -> mesh.freeBuffers());
    }
    
    /**
     * Frees the OpenGL texture objects used by this class.
     */
    public void freeTextures() {
        for(Texture texture : textures) texture.freeTexture();
    }
    
    /*
    vec3 direction = normalize(light.position);
    float diff     = max(dot(normal, direction), -light.contrast);
    vec3 diffuse   = diff * light.ambient * light.diffuse;
    
    return (light.ambient + diffuse) * light.brightness;
    */
    
    /*
    vec3 direction = normalize(uLights[0].position);
    
    vec3 norm    = normalize(ioNormal);
    float diff   = max(dot(norm, direction), 0);
    vec3 diffuse = diff * uLights[0].diffuse * uLights[0].brightness;
    vec3 ambient = uLights[0].ambient * uLights[0].contrast;

    //Calculate shadows.
    float dotLightNormal = dot(direction, norm);
    float shadow         = calcShadow(dotLightNormal);
    vec3 lighting        = (shadow * diffuse + ambient) * ioColor;
    */
    
}