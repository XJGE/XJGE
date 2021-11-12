package org.xjge.scenes;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.LightSource;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Model;

/**
 * Nov 10, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityTeapot extends Entity {

    Model teapot;
    
    EntityTeapot(float x, float y, float z) {
        super(new Vector3f(x, y, z));
        
        teapot = new Model("mod_teapot.fbx");
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        teapot.delocalizeNormal();
        
        //position.x += 0.05f;
        //position.z -= 0.05f;
        
        teapot.meshes.forEach(mesh -> {
            mesh.modelMatrix.translation(position);
            mesh.modelMatrix.rotateX((float) Math.toRadians(-135));
            mesh.modelMatrix.rotateY((float) Math.toRadians(90));
            mesh.modelMatrix.scale(0.15f);
        });
    }

    @Override
    public void render(GLProgram glProgram, Camera camera, LightSource[] lights, int numLights) {
        teapot.render(glProgram, lights, numLights);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera, LightSource[] lights, int numLights) {
    }

    @Override
    public void renderShadow(GLProgram depthProgram) {
        teapot.renderShadow(depthProgram);
    }

    @Override
    protected void destroy() {
    }

}
