package org.xjge.test;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import org.xjge.core.Command;
import org.xjge.core.Entity;
import org.xjge.core.Logger;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CommandMove extends Command {
    
    private float leftX;
    private float leftY;
    
    private ComponentMovable movable;
    private final Vector3f entityPosition;
    private final CameraOverhead camera;
    
    CommandMove(Entity entity, CameraOverhead camera) {
        this.camera = camera;
        
        entityPosition = (entity.hasComponent(ComponentPosition.class))
                       ? entity.getComponent(ComponentPosition.class).position
                       : new Vector3f();
        
        try {
            movable = entity.getComponent(ComponentMovable.class);
        } catch(Exception exception) {
            Logger.logWarning("No movable component found attached " + 
                              "to this entity: " + entity.uuid + " " + entity, 
                              exception);
        }
    }
    
    @Override
    public void execute(double targetDelta, double trueDelta) {
        if(axisMoved() && movable != null) {
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
                entityPosition.fma(((float) trueDelta) * movable.moveSpeed * mag, moveDir, entityPosition);

                //Smooth facing toward movement direction
                float desiredYaw  = (float)Math.toDegrees(Math.atan2(moveDir.z, moveDir.x));
                movable.facingYaw = lerpAngle(movable.facingYaw, desiredYaw, ((float) trueDelta) * 10f);
            }
        }
    }

    private float lerpAngle(float a, float b, float t) {
        float diff = ((b - a + 540f) % 360f) - 180f;
        return (a + diff * Math.min(1f, t) + 360f) % 360f;
    }
    
}