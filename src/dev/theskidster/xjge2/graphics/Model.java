package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.LightSource;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.core.XJGE;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix3f;
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

    public static final int MAX_TEXTURES = 4;
    
    private AIScene aiScene;
    private final Vector3f noValue = new Vector3f();
    private final Matrix3f normal  = new Matrix3f();
    
    private Mesh[] meshes;
    private Texture[] textures;
    
    public Model(String filename, int aiArgs) {
        try(InputStream file = Model.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
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
        
        aiScene = aiImportFileEx((XJGE.getFilepath() + filename), aiArgs, aiFileIO);
        
        if(aiScene == null) {
            MemoryUtil.memFree(modelBuf);
            throw new IllegalStateException(aiGetErrorString());
        } else {
            MemoryUtil.memFree(modelBuf);
            
            parseMeshData(aiScene.mMeshes());
            parseTextureData(aiScene.mMaterials());
        }
    }
    
    private void parseMeshData(PointerBuffer meshBuf) throws Exception {
        meshes = new Mesh[aiScene.mNumMeshes()];
        
        for(int i = 0; i < meshes.length; i++) {
            AIMesh aiMesh = AIMesh.create(meshBuf.get(i));
            meshes[i]     = new Mesh(aiMesh); //TODO add bones.
        }
    }
    
    private void parseTextureData(PointerBuffer materialBuf) throws Exception {
        if(aiScene.mNumMaterials() > MAX_TEXTURES) {
            textures = new Texture[MAX_TEXTURES];
            Logger.logWarning(
                    "Invalid number of textures. Limit of " + MAX_TEXTURES + 
                    " permitted, found " + aiScene.mNumMaterials(), 
                    null);
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
    
    public void render(GLProgram glProgram, LightSource[] lights, int numLights) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        for(Mesh mesh : meshes) {
            glBindTexture(GL_TEXTURE_2D, textures[mesh.texIndex].handle);
            glBindVertexArray(mesh.vao);
            
            glProgram.setUniform("uType", 5);
            glProgram.setUniform("uModel", false, mesh.modelMatrix);
            glProgram.setUniform("uNormal", true, normal);
            glProgram.setUniform("uNumLights", numLights);
            
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
            
            glDrawElements(GL_TRIANGLES, mesh.indices.capacity(), GL_UNSIGNED_INT, 0);
        }
        
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        
        ErrorUtils.checkGLError();
    }
    
}