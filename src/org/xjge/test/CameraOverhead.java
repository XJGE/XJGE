package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import org.xjge.core.Logger;
import org.xjge.core.Puppet;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {
    
    private float pitch = 35f;
    private float yaw = -90f;
    
    private float rotationSpeed = 2.5f;
    
    private final Puppet puppet = new Puppet("camera");
    private Vector3f nextPosition = new Vector3f();
    
    public CameraOverhead() {
        super(false);
        fov = 40;
        direction.set(0, 0, -1);
        
        puppet.commands.put(Control.RIGHT_STICK_X, new CommandRotateCamera());
        puppet.commands.put(Control.RIGHT_STICK_Y, new CommandRotateCamera());
    }
    
    class CommandRotateCamera extends Command {
        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved()) {
                if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                    yaw += getInputValue() * rotationSpeed;
                } else if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                    pitch += getInputValue() * rotationSpeed;
                }
            }
        }
    }
    
    @Override
    public void update() {
        pitch = Math.max(5f, Math.min(80f, pitch)); //clamp pitch to avoid flipping
        
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
                viewMatrix.setLookAt(position, nextPosition, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void follow(Entity entity) {
        try {
            nextPosition = entity.getComponent(ComponentPosition.class).position;
            puppet.setInputDevice(entity.getComponent(ComponentUnit.class).inputDeviceID);
        } catch(Exception exception) {
            Logger.logWarning("Uanble to focus camera on entity " + entity + 
                              " (" + entity.uuid + "). Object is missing one" +
                              "or more required components", exception);
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
    
}