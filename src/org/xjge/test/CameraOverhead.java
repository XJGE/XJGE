package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {
    
    float pitch = 35f;
    float yaw = -90f;
    
    float rotationSpeed = 2.5f;
    
    private Vector3f nextPosition = new Vector3f();
    private final Vector3f adjustedTarget = new Vector3f();
    
    public CameraOverhead() {
        super(false);
        fov = 40;
        direction.set(0, 0, -1);
    }
    
    @Override
    public void update() {
        pitch = Math.max(15f, Math.min(80f, pitch)); //clamp pitch to avoid flipping
        
        float distance = 6f;
        
        //compute offset from yaw/pitch spherical coords
        float offsetX = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float) (distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        
        position.set(nextPosition.x - offsetX, nextPosition.y + offsetY, nextPosition.z - offsetZ);
        direction.set(nextPosition).sub(position).normalize();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                adjustedTarget.set(nextPosition).add(0, 0.5f, 0);
                viewMatrix.setLookAt(position, adjustedTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void follow(Entity entity) {
        nextPosition = entity.getComponent(ComponentPosition.class).position;
    }
    
    public Vector3f getFlatForward() {
        Vector3f forward = new Vector3f(direction).normalize();
        forward.y = 0;
        
        return forward.normalize();
    }

    public Vector3f getRight() {
        Vector3f forward = getFlatForward();
        Vector3f right = new Vector3f();
        forward.cross(up, right);
        
        return right.normalize();
    }
    
}