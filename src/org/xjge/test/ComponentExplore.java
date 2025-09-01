package org.xjge.test;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import org.xjge.core.Command;
import org.xjge.core.Component;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import org.xjge.core.Puppet;
import org.xjge.core.Window;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentExplore extends Component {
    
    float moveSpeed = 5f;
    float facingYaw;
    
    private final Puppet puppet;
    
    private final Entity entity;
    private final CameraExplore camera;
    
    ComponentExplore(int inputDeviceID, Entity entity, CameraExplore camera) {
        this.entity = entity;
        this.camera = camera;
        
        Window.setViewportCamera(inputDeviceID, camera);
        camera.follow(entity);
        
        puppet = new Puppet("explore_" + inputDeviceID);
        
        puppet.commands.put(Control.LEFT_STICK_X,  new Move());
        puppet.commands.put(Control.LEFT_STICK_Y,  new Move());
        puppet.commands.put(Control.RIGHT_STICK_X, new RotateCamera());
        puppet.commands.put(Control.RIGHT_STICK_Y, new RotateCamera());
        
        puppet.setInputDevice(inputDeviceID);
    }
    
    private class Move extends Command {

        private float leftX;
        private float leftY;
        
        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved() && entity.hasComponent(ComponentPosition.class)) {
                if(getButtonID() == GLFW_GAMEPAD_AXIS_LEFT_X) {
                    leftX = getInputValue();
                } else if (getButtonID() == GLFW_GAMEPAD_AXIS_LEFT_Y) {
                    leftY = -getInputValue();
                }

                if(leftX == 0 && leftY == 0) return; //Analog stick is centered

                //Combine raw stick input (no pre-normalize)
                Vector3f moveDir = new Vector3f();
                moveDir.fma(leftY, camera.getFlatForward(), moveDir);
                moveDir.fma(leftX, camera.getRight(), moveDir);

                if(moveDir.lengthSquared() > 0.0001f) {
                    moveDir.normalize();
                    
                    Vector3f position = entity.getComponent(ComponentPosition.class).position;

                    //Scale movement by *stick magnitude* (for analog fidelity)
                    float mag = (float) Math.sqrt(leftX * leftX + leftY * leftY);
                    position.fma(((float) trueDelta) * moveSpeed * mag, moveDir, position);

                    //Smooth facing toward movement direction
                    float desiredYaw  = (float)Math.toDegrees(Math.atan2(moveDir.z, moveDir.x));
                    facingYaw = lerpAngle(facingYaw, desiredYaw, ((float) trueDelta) * 10f);
                }
            }
        }
        
        private float lerpAngle(float a, float b, float t) {
            float diff = ((b - a + 540f) % 360f) - 180f;
            return (a + diff * Math.min(1f, t) + 360f) % 360f;
        }
        
    }
    
    private class RotateCamera extends Command {

        @Override
        public void execute(double targetDelta, double trueDelta) {
            if(axisMoved()) {
                if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                    camera.yaw += getInputValue() * camera.rotationSpeed;
                } else if(getButtonID() == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                    camera.pitch += getInputValue() * camera.rotationSpeed;
                }
            }
        }
        
    }
    
}