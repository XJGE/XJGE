package org.xjge.graphics;

import java.util.LinkedHashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.xjge.core.Asset;
import org.xjge.core.AssetReloadListener;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class JointAttachment extends EntityComponent implements AssetReloadListener {

    private final Map<String, Joint> joints = new LinkedHashMap<>();
    
    /**
     * Serves as an attachment point
     */
    private static final class Joint {
        
        final int boneIndex;

        final Matrix4f localOffset    = new Matrix4f().identity();
        final Matrix4f worldTransform = new Matrix4f();
        
        Joint(int boneIndex) {
            this.boneIndex = boneIndex;
        }
    }
    
    public JointAttachment(Model model) {
        generate(model);
    }
    
    private void generate(Model model) {
        model.addReloadListener(this);
        
        for(var bone : model.getSkeleton().getBones()) {
            var joint = new Joint(model.getSkeleton().getBoneIndex(bone.name));
            joints.put(bone.name, joint);
        }
    }
    
    public void update(Transform transform, ModelAnimator animator) {
        for(var joint : joints.values()) {
            joint.worldTransform.identity()
                .mul(transform.getModelMatrix())
                .mul(animator.getBoneTransforms()[joint.boneIndex])
                .mul(joint.localOffset);
        }
    }
    
    public Matrix4fc getLocalOffset(String boneName) {
        return joints.containsKey(boneName) ? joints.get(boneName).localOffset : null;
    }
    
    public Matrix4fc getWorldTransform(String boneName) {
        return joints.containsKey(boneName) ? joints.get(boneName).worldTransform : null;
    }
    
    @Override
    public void onAssetReload(Asset asset) {
        if(asset instanceof Model model) generate(model);
    }
    
}