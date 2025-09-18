package org.xjge.test;

import java.util.Map;
import org.joml.Vector3f;
import org.xjge.core.Camera;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class CameraMelee extends Camera {

    private float pitch = 45f;
    private float yaw = -90f;
    
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
        
        //Find midpoint between units
        midpoint.set(attackerPos).add(defenderPos).mul(0.5f);
        desiredTarget.set(midpoint).add(0, 0.5f, 0);
        
        //Adjust camera distance based on unit spacing
        float baseDist = attackerPos.distance(defenderPos) * 0.75f + 4f;
        distance = Math.max(4f, Math.min(8f, baseDist));
        
        //Spherical offset
        float offsetX = (float)(distance * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        float offsetY = (float)(distance * Math.sin(Math.toRadians(pitch)));
        float offsetZ = (float)(distance * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        
        Vector3f desiredPos = new Vector3f(
            desiredTarget.x - offsetX,
            desiredTarget.y + offsetY,
            desiredTarget.z - offsetZ
        );
        
        //Smooth transition
        smoothedPos.lerp(desiredPos, (float)(targetDelta * 5.0)); //Tweak factor
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
    
    public void focus(Vector3f attacker, Vector3f defender) {
        attackerPos.set(attacker);
        defenderPos.set(defender);
    }
    
    public void setAngles(float newYaw, float newPitch) {
        yaw = newYaw;
        pitch = newPitch;
    }

}