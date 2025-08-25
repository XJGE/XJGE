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
    
    private final float moveSpeed = 5f;
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
            
            applyMovement((float) trueDelta);
        }
    }
    
    private void applyMovement(float deltaTime) {
        if(leftX == 0 && leftY == 0) return;

        //camera-relative basis
        Vector3f forward = camera.getFlatForward();
        Vector3f right   = camera.getRight();

        //combine into world move direction
        Vector3f moveDir = new Vector3f();
        moveDir.fma(leftY, forward, moveDir);
        moveDir.fma(leftX, right, moveDir);

        if(moveDir.lengthSquared() > 0.0001f) {
            moveDir.normalize();
            
            unit.position.fma(deltaTime * moveSpeed, moveDir, unit.position);

            //rotate unit to face movement direction
            //unit.facingYaw = (float)Math.toDegrees(Math.atan2(moveDir.z, moveDir.x));
        }
    }

}