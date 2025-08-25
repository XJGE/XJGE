package org.xjge.test;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import org.xjge.core.Command;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CommandMove extends Command {
    
    private float leftX;
    private float leftY;
    
    private final ComponentUnit unit;
    private final CameraOverhead camera;
    
    CommandMove(ComponentUnit unit, CameraOverhead camera) {
        this.unit   = unit;
        this.camera = camera;
    }
    
    @Override
    public void execute(double targetDelta, double trueDelta) {
        if(axisMoved()) {
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

                //Scale movement by *stick magnitude* (for analog fidelity)
                float mag = (float)Math.sqrt(leftX * leftX + leftY * leftY);
                unit.position.fma(((float) trueDelta) * unit.moveSpeed * mag, moveDir, unit.position);

                //Smooth facing toward movement direction
                float desiredYaw = (float)Math.toDegrees(Math.atan2(moveDir.z, moveDir.x));
                unit.facingYaw = lerpAngle(unit.facingYaw, desiredYaw, ((float) trueDelta) * 10f);
            }
        }
    }

    private float lerpAngle(float a, float b, float t) {
        float diff = ((b - a + 540f) % 360f) - 180f;
        return (a + diff * Math.min(1f, t) + 360f) % 360f;
    }
    
}