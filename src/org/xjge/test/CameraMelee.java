package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraMelee extends Camera {

    private float pitch = 45f;
    private float yaw = -90f;
    private float targetYaw = yaw;
    private float targetPitch = pitch;
    
    private float distance = 5f;
    
    private final Vector3f attackerPos = new Vector3f();
    private final Vector3f defenderPos = new Vector3f();
    private final Vector3f midpoint    = new Vector3f();
    private final Vector3f smoothedPos = new Vector3f();
    
    private final Vector3f desiredTarget = new Vector3f();
    
    CameraMelee() {
        super(false);
    }
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
        pitch = Math.max(15f, Math.min(80f, pitch));

        //Midpoint between units
        midpoint.set(attackerPos).add(defenderPos).mul(0.5f);
        desiredTarget.set(midpoint).add(0, 0.5f, 0);

        //Adjust camera distance based on spacing
        float baseDist = attackerPos.distance(defenderPos) * 0.75f + 4f;
        distance = Math.max(4f, Math.min(8f, baseDist));

        //Interpolate yaw/pitch toward targets
        yaw   = XJGE.lerp(yaw,   targetYaw,   (float)(targetDelta * 5.0));
        pitch = XJGE.lerp(pitch, targetPitch, (float)(targetDelta * 5.0));

        //Spherical offset
        float offsetX = (float)(distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float)(distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float)(distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));

        Vector3f desiredPos = new Vector3f(
            desiredTarget.x - offsetX,
            desiredTarget.y + offsetY,
            desiredTarget.z - offsetZ
        );

        //Smoothly interpolate to desired camera position
        smoothedPos.lerp(desiredPos, (float)(targetDelta * 5.0));
        position.set(smoothedPos);

        direction.set(desiredTarget).sub(position).normalize();
    }

    @Override
    protected void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if(glProgram.containsUniform("uView")) {
                glProgram.use();
                viewMatrix.setLookAt(position, desiredTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }
    
    public void focus(Vector3f attackerPos, Vector3f defenderPos, float lerp) {
        if(this.attackerPos.lengthSquared() == 0f && this.defenderPos.lengthSquared() == 0f) {
            //First pass: snap to real positions
            this.attackerPos.set(attackerPos);
            this.defenderPos.set(defenderPos);
        } else {
            this.attackerPos.lerp(attackerPos, lerp);
            this.defenderPos.lerp(defenderPos, lerp);
        }
    }
    
    public void setAngles(float newYaw, float newPitch, float lerp) {
        if(targetYaw == yaw && targetPitch == pitch) {
            //First pass: snap values
            this.targetYaw = newYaw;
            this.targetPitch = newPitch;
        } else {
            this.targetYaw   = XJGE.lerp(this.targetYaw,   newYaw,   lerp);
            this.targetPitch = XJGE.lerp(this.targetPitch, newPitch, lerp);
        }
    }

}