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
    private float distance = 5f;

    private final Vector3f attackerPos = new Vector3f();
    private final Vector3f defenderPos = new Vector3f();
    private final Vector3f smoothedPos = new Vector3f();
    private final Vector3f desiredTarget = new Vector3f();

    private final Vector3f offsetBehind = new Vector3f(); // Computed behind attacker
    private final Vector3f forward = new Vector3f();      // Direction attacker is facing

    CameraMelee() {
        super(false);
    }

    @Override
    protected void update(double targetDelta, double trueDelta) {
        // Slightly above defender for framing
        desiredTarget.set(defenderPos).add(0, 1.0f, 0);

        // Compute direction from attacker to defender
        forward.set(defenderPos).sub(attackerPos).normalize();

        // Position the camera behind the attacker
        offsetBehind.set(forward).mul(-distance); // move opposite to facing direction
        offsetBehind.y = distance * 0.5f;        // lift camera slightly

        Vector3f desiredPos = new Vector3f(attackerPos).add(offsetBehind);

        // Smooth interpolation
        smoothedPos.lerp(desiredPos, (float) (targetDelta * 5.0));
        position.set(smoothedPos);

        // Camera always looks at the defender
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

    public void focus(Vector3f attacker, Vector3f defender) {
        attackerPos.set(attacker);
        if (defender != null) {
            defenderPos.set(defender);
        }
    }

    public void setPitch(float newPitch) {
        pitch = Math.max(15f, Math.min(80f, newPitch));
    }

}