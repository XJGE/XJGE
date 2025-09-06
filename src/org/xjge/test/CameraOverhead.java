package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import org.xjge.core.Puppet;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {
    
    float pitch = 25f;
    float yaw = -90f;
    
    float rotationSpeed = 2.5f;
    
    private Vector3f nextPosition = new Vector3f();
    private final Vector3f adjustedTarget = new Vector3f();
    
    private final Puppet puppet = new Puppet("camera");
    
    public CameraOverhead() {
        super(false);
        fov = 40;
        direction.set(0, 0, -1);
        
        puppet.commands.put(Control.RIGHT_STICK_X, new RotateCameraYaw());
        puppet.commands.put(Control.RIGHT_STICK_Y, new RotateCameraPitch());
    }
    
    @Override
    public void update() {
        pitch = Math.max(15f, Math.min(80f, pitch)); //clamp pitch to avoid flipping
        
        float distance = 5f;
        
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
        
        if(entity.hasComponent(ComponentUnit.class)) {
            puppet.setInputDevice(entity.getComponent(ComponentUnit.class).inputDeviceID);
        }
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
    
    private class RotateCameraYaw extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved()) {
                yaw += getInputValue() * rotationSpeed;
            }
        }
        
    }
    
    private class RotateCameraPitch extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved()) pitch += getInputValue() * rotationSpeed;
        }
        
    }
    
}