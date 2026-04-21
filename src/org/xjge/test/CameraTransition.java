package org.xjge.test;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Window;
import org.xjge.core.XJGE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public class CameraTransition extends Camera {

    private final float duration;
    private float deltaTime;
    private float blendedFOV;
    
    private final Camera previous;
    private final Camera next;
    
    private final Vector3f blendedPosition  = new Vector3f();
    private final Vector3f blendedDirection = new Vector3f();
    private final Vector3f tempFront        = new Vector3f();
    
    CameraTransition(Camera previous, Camera next, float duration) {
        this.previous = previous;
        this.next     = next;
        this.duration = duration;
    }
    
    @Override
    protected void update(double targetDelta, double trueDelta) {
        deltaTime += trueDelta;
        float t = Math.min(1f, deltaTime / duration);
        float eased = easeInOutQuad(t);
        
        blendedFOV = XJGE.lerp(previous.fov, next.fov, eased);
        blendedPosition.set(previous.getPosition()).lerp(next.getPosition(), eased);
        blendedDirection.set(previous.getDirection()).lerp(next.getDirection(), eased);
        
        if(eased == 1f) Window.setViewportCamera(GLFW_JOYSTICK_1, next);
    }

    @Override
    protected void buildViewMatrix(Matrix4f viewMatrix) {
        viewMatrix.setLookAt(blendedPosition,
                            tempFront.set(blendedDirection).add(blendedPosition),
                            up);
    }

    @Override
    protected void buildProjectionMatrix(Matrix4f projectionMatrix, int viewportWidth, int viewportHeight) {
        projectionMatrix.setPerspective((float) Math.toRadians(blendedFOV), 
                                        (float) viewportWidth / viewportHeight, 
                                        0.1f, Float.POSITIVE_INFINITY);
    }
    
    private float easeInOutQuad(float t) {
        return (t < 0.5f) ? 2*t*t : -1+(4-2*t)*t;
    }

}