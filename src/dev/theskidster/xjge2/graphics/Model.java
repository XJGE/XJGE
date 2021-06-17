package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileIO;
import static org.lwjgl.assimp.Assimp.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 16, 2021
 */

public class Model {

    public Model(String filename, int aiArgs) {
        try(InputStream file = Model.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
            loadModel(file, aiArgs);
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
    
    private void loadModel(InputStream file, int aiArgs) throws Exception {
        byte[] data = file.readAllBytes();
        
        ByteBuffer modelBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
        AIFileIO aiFileIO   = AIFileIO.create();
        AIFile aiFile       = AIFile.create();
        
        
    }
    
}