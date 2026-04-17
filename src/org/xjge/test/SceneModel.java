package org.xjge.test;

import org.xjge.graphics.ModelRenderer;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
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
import org.xjge.core.UIManager;
import org.xjge.graphics.Shader;
import org.xjge.graphics.Texture;
import org.xjge.graphics.Model;
import org.xjge.graphics.ModelAnimator;
import org.xjge.graphics.Transform;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class SceneModel extends Scene {

    private Model testModel;
    private Model testModel2;
    private Entity floor;
    private Entity testEntity;
    private Entity testEntity2;
    private ModelAnimator animator;
    private Light worldLight;
    private Skybox skybox;
    
    public SceneModel() {
        super("test_model3");
        
        var right  = Texture.load("sky_right.png");
        var left   = Texture.load("sky_left.png");
        var top    = Texture.load("sky_top.png");
        var bottom = Texture.load("sky_bottom.png");
        var front  = Texture.load("sky_front.png");
        var back   = Texture.load("sky_back.png");
        
        skybox = new Skybox(right, left, top, bottom, front, back, false);
        setSkybox(skybox);
        
        var prism = new Prism(1, -0.5f, 0, 10, 1, 10);
        //prism.color.copy(Color.BLUE);
        floor = new Entity().addComponent(prism);
        addEntity(floor);
        
        var transform2 = new Transform();
        testModel2  = Model.load("person.fbx");
        testEntity2 = new Entity().addComponent(new ModelRenderer(testModel2))
                                  .addComponent(new ModelAnimator(testModel2))
                                  .addComponent(transform2);
        transform2.rotation.rotateX((float) Math.toRadians(-90));
        addEntity(testEntity2);
        
        testModel = Model.load("yshtola.fbx");
        var transform = new Transform();
        transform.position.z = -1.5f;
        animator = new ModelAnimator(testModel);
        testEntity = new Entity().addComponent(new ModelRenderer(testModel))
                                 .addComponent(transform)
                                 .addComponent(animator);
        addEntity(testEntity);
        
        outputModelData(testModel2);
        
        //var testAnimation = testModel.getAnimation("wave");
        
        testEntity.getComponent(ModelAnimator.class).play("wave");
        //testEntity.getComponent(ModelAnimator.class).setLooping(false);
        //testEntity.getComponent(ModelAnimator.class).setSpeed(0.5f);
        
        worldLight = LightingSystem.request();
        worldLight.type = LightType.WORLD;
        worldLight.position.set(0, 4.5f, 3);
        worldLight.brightness = 7f;
        
        UIManager.addWidget(GLFW_JOYSTICK_1, "animation_control", new UIAnimationWidget(animator));
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        for(var entity : queryEntities(ModelAnimator.class)) {
            entity.getComponent(ModelAnimator.class).update((float) targetDelta);
        }
    }

    @Override
    public void render(int viewportID, Camera camera, int depthTexHandle) {
        for(var entity : queryEntities(Prism.class)) {
            entity.getComponent(Prism.class).render(camera);
        }
        
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);
        for(var entity : queryEntities(ModelRenderer.class, Transform.class, ModelAnimator.class)) {
            entity.getComponent(ModelRenderer.class).render( 
                    entity.getComponent(Transform.class), 
                    entity.getComponent(ModelAnimator.class),
                    camera);
        }
        //glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void renderShadows(Shader depthProgram) {
    }

    @Override
    public void exit() {
    }

    private void outputModelData(Model model) {
        System.out.println("Materials:");
        for(var material : model.getMaterials()) {
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
        for(var mesh : model.getMeshes()) {
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
        for(var animName : model.getAnimaitonNames()) {
            var animation = model.getAnimation(animName);
            System.out.println(" Name: " + animName);
            System.out.println("  Duration: " + animation.duration);
            System.out.println("  Ticks Per Second: " + animation.ticksPerSecond);
            System.out.println("  Num Keyframes: " + animation.keyframes.size());
        }
        System.out.println();
        
        System.out.println("Skeleton:");
        System.out.println(" Bones: ");
        for(var bone : model.getSkeleton().bones) {
            System.out.println("  Name: " + bone.name);
            System.out.println("  Parent Index: " + bone.parentIndex);
            System.out.println("  Offset Matrix: ");
            System.out.println("   " + bone.offsetMatrix);
        }
        System.out.println(" Bone Map:");
        model.getSkeleton().boneMap.forEach((boneName, index) -> System.out.println("  " + boneName + ", " + index));
        System.out.println();
    }
    
}