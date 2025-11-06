package org.xjge.core;

import org.xjge.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * Created: May 8, 2021
 * <p>
 * A camera object provided by the engine that permits free traversal of the 
 * game world.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Noclip extends Camera {

    boolean enabled;
    
    boolean[] pressed = new boolean[4];
    
    float speed = 0.25f;
    private float pitch;
    private float yaw = -90f;
    private final float sensitivity = 0.10f;
    
    double prevX;
    double prevY;
    
    private final Vector3f tempFront = new Vector3f();
    private final Vector3f tempRight = new Vector3f();
    private final Vector3f tempDirec = new Vector3f();
    
    /**
     * Creates a new camera that may be used to freely traverse the game world.
     */
    Noclip() {
        super(false);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        if(pressed[0]) position.add(direction.mul(speed, tempDirec));
        if(pressed[1]) position.sub(direction.cross(up, tempRight).normalize().mul(speed));
        if(pressed[2]) position.sub(direction.mul(speed, tempDirec));
        if(pressed[3]) position.add(direction.cross(up, tempRight).normalize().mul(speed));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                viewMatrix.setLookAt(position, position.add(direction, tempFront), up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }

    /**
     * Applies sensitivity preferences to the cameras directional changes.
     * 
     * @param currValue the current position of the mouse cursor along an axis
     * @param prevValue the previous position of the mouse cursor along an axis
     * 
     * @return a value denoting the intensity of the cameras look movement
     */
    private float getChangeIntensity(double currValue, double prevValue) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    /**
     * Sets the current direction the camera will face using the position of 
     * the mouse cursor inside the applications {@link Window}.
     * 
     * @param xPos the position of the cursor along the x-axis
     * @param yPos the position of the cursor along the y-axis
     */
    void setDirection(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            yaw   += getChangeIntensity(xPos, prevX) * 2;
            pitch += getChangeIntensity(yPos, prevY) * 2;
            
            if(pitch > 89f)  pitch = 89;
            if(pitch < -89f) pitch = -89;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
}