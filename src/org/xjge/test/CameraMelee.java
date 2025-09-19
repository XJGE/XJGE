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

    private float distance = 4f;
    private float height   = 1.5f;

    private final Vector3f attackerPos = new Vector3f();
    private final Vector3f defenderPos = new Vector3f();
    private final Vector3f smoothedPos = new Vector3f();
    private final Vector3f desiredTarget = new Vector3f();

    CameraMelee() {
        super(false);
    }

    @Override
    protected void update(double targetDelta, double trueDelta) {
        desiredTarget.set(attackerPos).add(defenderPos).mul(0.5f).add(0, 0.3f, 0);
        
        //Direction from attacker to defender (XZ plane)
        Vector3f dir = new Vector3f(defenderPos).sub(attackerPos);
        dir.y = 0;
        if(dir.lengthSquared() < 0.0001f) dir.set(0, 0, 1);

        dir.normalize();

        //Perpendicular vectors on both sides
        Vector3f perp1 = new Vector3f(-dir.z, 0, dir.x).normalize();
        Vector3f perp2 = new Vector3f(dir.z, 0, -dir.x).normalize();
        
        Vector3f candidate1 = new Vector3f(desiredTarget).add(perp1.mul(distance)).add(0, height, 0);
        Vector3f candidate2 = new Vector3f(desiredTarget).add(perp2.mul(distance)).add(0, height, 0);
        
        System.out.println("cam pos: " + position);
        System.out.println("perp1: " + perp1 + " " + candidate1.distanceSquared(position));
        System.out.println("perp2: " + perp2 + " " + candidate2.distanceSquared(position));
        
        Vector3f desiredPos = candidate1.distanceSquared(position) < candidate2.distanceSquared(position)
                            ? candidate1
                            : candidate2;
        
        smoothedPos.lerp(desiredPos, (float)(targetDelta * 10.0));
        position.set(smoothedPos);
        
        direction.set(desiredTarget).sub(position).normalize();
    }

    @Override
    protected void render(Map<String, GLProgram> glPrograms) {
        glPrograms.values().forEach(glProgram -> {
            if (glProgram.containsUniform("uView")) {
                glProgram.use();
                viewMatrix.setLookAt(position, desiredTarget, up);
                glProgram.setUniform("uView", false, viewMatrix);
            }
        });
    }

    public void focus(Vector3f attackerPos, Vector3f defenderPos) {
        this.attackerPos.set(attackerPos);
        this.defenderPos.set(defenderPos);
    }

}