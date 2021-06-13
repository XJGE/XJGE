package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

final class FreeCam extends Camera {

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
    
    FreeCam() {
        super(false);
    }

    @Override
    void update() {
        if(pressed[0]) position.add(direction.mul(speed, tempDirec));
        if(pressed[1]) position.sub(direction.cross(up, tempRight).normalize().mul(speed));
        if(pressed[2]) position.sub(direction.mul(speed, tempDirec));
        if(pressed[3]) position.add(direction.cross(up, tempRight).normalize().mul(speed));
    }

    @Override
    void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            glProgram.use();
            
            viewMatrix.setLookAt(position, position.add(direction, tempFront), up);
            glProgram.setUniform("uView", false, viewMatrix);
        });
    }

    private float getChangeIntensity(double currValue, double prevValue) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    public void setSpeedBoostEnabled(boolean enable) {
        speed = (enable) ? 0.75f : 0.25f;
    }
    
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