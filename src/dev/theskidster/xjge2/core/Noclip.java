package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

//Created: May 8, 2021

/**
 * A camera object provided by the engine that permits free traversal of the 
 * game world.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
final class Noclip extends Camera {

    private float speed = 0.25f;
    private float pitch;
    private float yaw               = -90f;
    private final float sensitivity = 0.10f;
    
    public double prevX;
    public double prevY;
    
    public boolean[] pressed = new boolean[4];
    
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
    public void update() {
        if(pressed[0]) position.add(direction.mul(speed, tempDirec));
        if(pressed[1]) position.sub(direction.cross(up, tempRight).normalize().mul(speed));
        if(pressed[2]) position.sub(direction.mul(speed, tempDirec));
        if(pressed[3]) position.add(direction.cross(up, tempRight).normalize().mul(speed));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            glProgram.use();
            
            viewMatrix.setLookAt(position, position.add(direction, tempFront), up);
            glProgram.setUniform("uView", false, viewMatrix);
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
     * Allows the camera to traverse the scene quicker.
     * 
     * @param enable if true, the camera will move faster
     */
    public void setSpeedBoostEnabled(boolean enable) {
        speed = (enable) ? 0.75f : 0.25f;
    }
    
    /**
     * Sets the current direction the camera will face using the position of 
     * the mouse cursor inside the applications {@link Window}.
     * 
     * @param xpos the position of the cursor along the x-axis
     * @param ypos the position of the cursor along the y-axis
     */
    public void setDirection(double xpos, double ypos) {
        if(xpos != prevX || ypos != prevY) {
            yaw   += getChangeIntensity(xpos, prevX) * 2;
            pitch += getChangeIntensity(ypos, prevY) * 2;
            
            if(pitch > 89f)  pitch = 89;
            if(pitch < -89f) pitch = -89;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xpos;
            prevY = ypos;
        }
    }
    
}