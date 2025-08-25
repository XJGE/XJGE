package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import org.xjge.core.Camera;
import org.xjge.core.Command;
import org.xjge.core.Control;
import org.xjge.core.Puppet;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraOverhead extends Camera {
    
    private float pitch = 36f;
    private float yaw = -90f;
    private float moveSpeed;
    private float moveLerp;
    
    private final Puppet puppet = new Puppet("camera");
    private final Vector3f temp = new Vector3f();
    private Vector3f nextPosition = new Vector3f();
    
    public CameraOverhead() {
        super(false);
        fov = 32;
        direction.set(0, 0, -1);
        
        puppet.commands.put(Control.RIGHT_STICK_X, new CommandRotateCamera());
        puppet.commands.put(Control.RIGHT_STICK_Y, new CommandRotateCamera());
    }
    
    class CommandRotateCamera extends Command {
    
        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved()) {
                if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                    yaw += getInputValue();
                    //System.out.println("X: " + getInputValue());
                } else if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                    pitch += getInputValue();
                    //System.out.println("Y: " + getInputValue());
                }
            }
        }

    }
    
    @Override
    public void update() {
        // Clamp pitch to avoid flipping
        pitch = Math.max(10f, Math.min(80f, pitch));
        
        // Desired distance behind the target
        float distance = 6f;
        
        // Compute offset from yaw/pitch spherical coords
        float offsetX = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float) (distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float) (distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        
        // Camera sits at target + offset
        position.set(nextPosition.x - offsetX,
                     nextPosition.y + offsetY,
                     nextPosition.z - offsetZ);

        // Look at target (the unit, not forward vector)
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
    
    public void setActiveUnit(ComponentUnit unit) {
        moveTo(new Vector3f(unit.position.x, unit.position.y, unit.position.z), 1f);
        puppet.setInputDevice(unit.inputDeviceID);
    }
    
    public void moveTo(Vector3f nextPosition, float speed) {
        this.nextPosition = nextPosition;
        moveSpeed         = speed;
        moveLerp          = 0;
    }
    
}