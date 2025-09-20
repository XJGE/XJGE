package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import static org.xjge.core.Input.KEY_MOUSE_COMBO;
import org.xjge.core.Puppet;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraFollow extends Camera {
    
    private boolean enableRotation;
    private boolean lookInversion;
    
    float pitch = 25f;
    float yaw = -90f;
    
    float rotationSpeed = 2.5f;
    
    private Vector3f nextPosition = new Vector3f();
    private final Vector3f adjustedTarget = new Vector3f();
    
    private final Puppet puppet = new Puppet("camera");
    
    public CameraFollow() {
        super(false);
        fov = 40;
        direction.set(0, 0, -1);
        
        puppet.commands.put(Control.R2, new EnableRotation());
        puppet.commands.put(Control.RIGHT_STICK_X, new RotateCameraYaw());
        puppet.commands.put(Control.RIGHT_STICK_Y, new RotateCameraPitch());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        pitch = Math.max(15f, Math.min(80f, pitch)); //clamp pitch to avoid flipping
        
        float distance = 4.5f;
        
        adjustedTarget.set(nextPosition).add(0, 0.8f, 0);
        
        //compute offset from yaw/pitch spherical coords
        float offsetX = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float) (distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        
        position.set(adjustedTarget.x - offsetX, adjustedTarget.y + offsetY, adjustedTarget.z - offsetZ);
        direction.set(adjustedTarget).sub(position).normalize();
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                viewMatrix.setLookAt(position, adjustedTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void follow(Entity entity) {
        nextPosition = entity.getComponent(ComponentPosition.class).position;
        
        if(entity.hasComponent(ComponentUnit.class)) {
            int inputDeviceID = entity.getComponent(ComponentUnit.class).inputDeviceID;
            rotationSpeed = (inputDeviceID == KEY_MOUSE_COMBO) ? 0.5f : 2.5f;
            lookInversion = inputDeviceID == KEY_MOUSE_COMBO;
            puppet.setInputDevice(inputDeviceID);
        }
        
        update(0.016, 0.016); //Fixes snap following unit movement into an unoccupied space
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
    
    private class EnableRotation extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(getDeviceID() == KEY_MOUSE_COMBO) {
                enableRotation = buttonPressed();
                Window.setInputMode(GLFW_CURSOR, enableRotation ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
            } else {
                enableRotation = true;
            }
        }
        
    }
    
    private class RotateCameraYaw extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved() && enableRotation) yaw += getInputValue() * rotationSpeed;
        }
        
    }
    
    private class RotateCameraPitch extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved() && enableRotation) {
                if(lookInversion) {
                    pitch -= getInputValue() * rotationSpeed;
                } else {
                    pitch += getInputValue() * rotationSpeed;
                }
            }
        }
        
    }
    
}