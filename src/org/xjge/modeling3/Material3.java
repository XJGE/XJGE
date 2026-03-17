package org.xjge.modeling3;

import java.nio.IntBuffer;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import org.lwjgl.system.MemoryStack;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class Material3 {
    
    public float metallic;
    public float roughness;
    public float opacity = 1f;
    
    public TransparencyMode transparencyMode = TransparencyMode.OPAQUE;
    
    public final Vector3f albedoColor = new Vector3f(1f);
    
    public String albedoMapFilename;
    public String normalMapFilename;
    public String metallicMapFilename;
    public String roughnessMapFilename;
    
    public Texture albedoMap;
    
    Material3(AIMaterial aiMaterial) {
        metallic  = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_METALLIC_FACTOR);
        roughness = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_ROUGHNESS_FACTOR);
        opacity   = extractMaterialValue(aiMaterial, Assimp.AI_MATKEY_OPACITY);
        albedoColor.set(extractAlbedoColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE));
        albedoMapFilename    = extractTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE);
        normalMapFilename    = extractTextureFilename(aiMaterial, Assimp.aiTextureType_NORMALS);
        metallicMapFilename  = extractTextureFilename(aiMaterial, Assimp.aiTextureType_METALNESS);
        roughnessMapFilename = extractTextureFilename(aiMaterial, Assimp.aiTextureType_DIFFUSE_ROUGHNESS);
        
        if(albedoMapFilename != null) albedoMap = Texture.load(albedoMapFilename, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT, GL_SRGB_ALPHA, GL_LINEAR);
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
    
    private Vector3f extractAlbedoColor(AIMaterial aiMaterial, CharSequence aiKey) {
        var aiColor = AIColor4D.create();
        
        int result = Assimp.aiGetMaterialColor(aiMaterial, aiKey, Assimp.aiTextureType_NONE, 0, aiColor);
        if(result != Assimp.aiReturn_SUCCESS) return new Vector3f(1f);
        
        return new Vector3f(aiColor.r(), aiColor.g(), aiColor.b());
    }
    
    private String extractTextureFilename(AIMaterial aiMaterial, int textureType) {
        var filepath = AIString.calloc();
        
        int result = Assimp.aiGetMaterialTexture(aiMaterial, textureType, 0, filepath, (IntBuffer) null, null, null, null, null, null);
        if(result != Assimp.aiReturn_SUCCESS) return null;
        
        return filepath.dataString();
    }
    
}