package org.xjge.test;

import java.util.Map;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Light;
import org.xjge.core.LightType;
import org.xjge.core.LightingSystem;
import org.xjge.core.Scene;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.Shader;
import org.xjge.modeling.Material2;
import org.xjge.modeling.Mesh2;
import org.xjge.modeling.Model2;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneModel extends Scene {

    Entity floor;
    Entity modelEntity;
    Light worldLight;
    
    public SceneModel() {
        super("test_model");
        
        XJGE.setClearColor(new Color(0.467f, 0.533f, 1f));
        worldLight = LightingSystem.request();
        worldLight.type = LightType.WORLD;
        worldLight.position.set(0, 4.5f, 3);
        worldLight.brightness = 7f;
        
        /*
        var pointLight = LightingSystem.request();
        pointLight.position.set(-4, 2, -3f);
        pointLight.color.copy(Color.RED);
        pointLight.type = LightType.POINT;
        */
        
        floor = new Entity().addComponent(new Prism(0, -1f, 0, 10, 0.5f, 10));
        addEntity(floor);
        
        Model2 model = Model2.load("yshtola.fbx");
        modelEntity = new Entity().addComponent(new ModelRenderer(model, 0, 0, -2));
        addEntity(modelEntity);
        
        System.out.println("Meshes:");
        for(Mesh2 mesh : model.meshes) {
            System.out.println(" position count: " + mesh.positions.length);
            System.out.println(" normals count: " + mesh.normals.length);
            System.out.println(" texCoord count: " + mesh.uvs.length);
            System.out.println(" tangent count: " + mesh.tangents.length);
            System.out.println(" index count: " + mesh.indices.length);
            System.out.println(" bone IDs: " + mesh.boneIDs.length);
            System.out.println(" bone weights: " + mesh.boneWeights.length);
            System.out.println();
        }
        
        System.out.println("Materials:");
        for(Material2 material : model.materials) {
            System.out.println(" roughness: " + material.roughness);
            System.out.println(" metallic: " + material.metallic);
            System.out.println(" albedoMap: " + material.albedoMapFilename);
            System.out.println(" normalMap: " + material.normalMapFilename);
            System.out.println(" roughnessMap: " + material.roughnessMapFilename);
            System.out.println(" metallicMap: " + material.metallicMapFilename);
            System.out.println();
        }
        
        System.out.println("MeshMaterialIndices:");
        for(int index : model.meshMaterialIndices) {
            System.out.println(" " + index);
        }
        System.out.println();
        
        System.out.println("Animations:");
        for(var animation : model.animations) {
            System.out.println(" name: " + animation.name);
            System.out.println(" duration: " + animation.duration);
            System.out.println(" ticks per second: " + animation.ticksPerSecond);
            System.out.println(" keyframes: " + animation.keyframes.size());
            if(!animation.keyframes.isEmpty()) {
                animation.keyframes.forEach((k,v) -> {
                    System.out.println("  bone name: " + k);
                    System.out.println("   positions: " + v.positions.length);
                    System.out.println("   rotations: " + v.rotations.length);
                    System.out.println("   scales: " + v.scales.length);
                });
                System.out.println();
            }
        }
        System.out.println();
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
    }

    @Override
    public void render(Map<String, Shader> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        for(var shape : queryEntities(Prism.class)) {
            shape.getComponent(Prism.class).render(camera);
        }
        
        for(var model : queryEntities(ModelRenderer.class)) {
            model.getComponent(ModelRenderer.class).render(camera);
        }
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
        removeEntity(floor.uuid);
        LightingSystem.release(worldLight);
    }

}