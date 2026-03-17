package org.xjge.test;

import org.xjge.modeling3.ModelRenderer3;
import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.graphics.Shader;
import org.xjge.modeling3.Model3;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneModel3 extends Scene {

    private Model3 testModel;
    private Entity testEntity;
    private Light worldLight;
    
    public SceneModel3() {
        super("test_model3");
        
        testModel = Model3.load("yshtola.fbx");
        
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
        
        testEntity = new Entity().addComponent(new ModelRenderer3(testModel));
        addEntity(testEntity);
        
        worldLight = LightingSystem.request();
        worldLight.type = LightType.WORLD;
        worldLight.position.set(0, 4.5f, 3);
        worldLight.brightness = 7f;
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        for(var entity : queryEntities(ModelRenderer3.class)) {
            entity.getComponent(ModelRenderer3.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

}