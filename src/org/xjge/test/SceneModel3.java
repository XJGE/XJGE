package org.xjge.test;

import org.xjge.modeling3.ModelRenderer3;
import java.util.Map;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.core.Skybox;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;
import org.xjge.modeling3.Model3;
import org.xjge.modeling3.ModelAnimator3;
import org.xjge.modeling3.SkeletalAnimation3;
import org.xjge.modeling3.Transform;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneModel3 extends Scene {

    private Model3 testModel;
    private Entity testEntity;
    private Light worldLight;
    private Skybox skybox;
    
    public SceneModel3() {
        super("test_model3");
        
        var right  = Texture.load("sky_right.png");
        var left   = Texture.load("sky_left.png");
        var top    = Texture.load("sky_top.png");
        var bottom = Texture.load("sky_bottom.png");
        var front  = Texture.load("sky_front.png");
        var back   = Texture.load("sky_back.png");
        
        skybox = new Skybox(right, left, top, bottom, front, back, false);
        setSkybox(skybox);
        
        testModel = Model3.load("mod_test.fbx");
        
        System.out.println("Materials:");
        for(var material : testModel.getMaterials()) {
            System.out.println(" Metallic: " + material.metallic);
            System.out.println(" Roughness: " + material.roughness);
            System.out.println(" Opacity: " + material.opacity);
            System.out.println(" Transparency Mode: " + material.transparencyMode);
            System.out.println(" Albedo Color: " + material.albedoColor);
            System.out.println(" Albedo Texture: " + material.albedoMapFilename);
            System.out.println(" Normal Texture: " + material.normalMapFilename);
            System.out.println(" Metallic Texture: " + material.metallicMapFilename);
            System.out.println(" Roughness Texture: " + material.roughnessMapFilename);
        }
        System.out.println();
        
        System.out.println("Meshes:");
        for(var mesh : testModel.getMeshes()) {
            System.out.println(" Vertex Data:");
            System.out.println("  Positions: " + mesh.positions.length);
            System.out.println("  Normals: " + mesh.normals.length);
            System.out.println("  Tangents: " + mesh.tangents.length);
            System.out.println("  UVs: " + mesh.uvs.length);
            System.out.println(" Other Data:");
            System.out.println("  Indices: " + mesh.indices.length);
            System.out.println("  Bone IDs: " + mesh.boneIDs.length);
            System.out.println("  Bone Weights: " + mesh.boneWeights.length);
        }
        System.out.println();
        
        System.out.println("Animations:");
        for(var animation : testModel.getAnimations()) {
            System.out.println(" Name: " + animation.name);
            System.out.println("  Duration: " + animation.duration);
            System.out.println("  Ticks Per Second: " + animation.ticksPerSecond);
            System.out.println("  Num Keyframes: " + animation.keyframes.size());
        }
        System.out.println();
        
        System.out.println("Skeleton:");
        System.out.println(" Bones: ");
        for(var bone : testModel.getSkeleton().bones) {
            System.out.println("  Name: " + bone.name);
            System.out.println("  Parent Index: " + bone.parentIndex);
            System.out.println("  Offset Matrix: ");
            System.out.println("   " + bone.offsetMatrix);
        }
        System.out.println(" Bone Map:");
        testModel.getSkeleton().boneMap.forEach((boneName, index) -> System.out.println("  " + boneName + ", " + index));
        System.out.println();
        
        var transform = new Transform();
        transform.position.z = -5f;
        
        testEntity = new Entity().addComponent(new ModelRenderer3())
                                 .addComponent(transform)
                                 .addComponent(new ModelAnimator3(testModel));
        addEntity(testEntity);
        
        SkeletalAnimation3 testAnimation = null;
        
        for(var animation : testModel.getAnimations()) {
            if(animation.name.equals("Wiggle")) testAnimation = animation;
        }
        
        if(testAnimation != null) {
            testEntity.getComponent(ModelAnimator3.class).play(testAnimation);
        }
        
        worldLight = LightingSystem.request();
        worldLight.type = LightType.WORLD;
        worldLight.position.set(0, 4.5f, 3);
        worldLight.brightness = 7f;
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        for(var entity : queryEntities(ModelAnimator3.class)) {
            entity.getComponent(ModelAnimator3.class).update((float) targetDelta);
        }
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        for(var entity : queryEntities(ModelRenderer3.class, Transform.class, ModelAnimator3.class)) {
            entity.getComponent(ModelRenderer3.class).render(
                    testModel, 
                    entity.getComponent(Transform.class), 
                    entity.getComponent(ModelAnimator3.class),
                    camera);
        }
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

}