package org.xjge.test;

import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Camera;
import org.xjge.core.Window;
import org.xjge.graphics.GLProgram;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ViewTransitioner {

    private boolean transitioning;
    
    private float transitionTime;
    private float transitionDuration;
    private float blendedFOV;
    
    private final Vector3f blendedPosition  = new Vector3f();
    private final Vector3f blendedDirection = new Vector3f();
    
    private Camera currentCamera;
    private Camera previousCamera;
    
    void setActiveCamera(Camera camera, float duration) {
        if(duration <= 0f || currentCamera == null) {
            Window.setViewportCamera(GLFW_JOYSTICK_1, camera);
            currentCamera = camera;
            transitioning = false;
        } else {
            if(currentCamera instanceof CameraFollow followCam) {
                followCam.unbindControllable();
            }
            
            previousCamera     = currentCamera;
            currentCamera      = camera;
            transitionDuration = duration;
            transitionTime     = 0f;
            transitioning      = true;
        }
    }
    
    void update(double targetDelta, double trueDelta) {
        if(transitioning) {
            if(currentCamera instanceof CameraMelee) {
                ((CameraMelee) currentCamera).update(targetDelta, trueDelta);
            }
            
            transitionTime += trueDelta;
            float t = Math.min(1f, transitionTime / transitionDuration);
            float eased = easeInOutQuad(t);

            blendedPosition.set(previousCamera.position).lerp(currentCamera.position, eased);
            blendedDirection.set(previousCamera.direction).lerp(currentCamera.direction, eased);
            blendedFOV = lerp(previousCamera.fov, currentCamera.fov, eased);

            if(t >= 1f) {
                Window.setViewportCamera(GLFW_JOYSTICK_1, currentCamera);
                transitioning = false;
            }
        }
    }
    
    void render(Map<String, GLProgram> glPrograms) {
        if(transitioning) {
            Matrix4f view = new Matrix4f().lookAt(blendedPosition,
                                                  new Vector3f(blendedPosition).add(blendedDirection),
                                                  new Vector3f(0,1,0));
            
            Matrix4f proj = new Matrix4f().setPerspective((float) Math.toRadians(blendedFOV), 
                                                          (float) Window.getResolutionWidth() / Window.getResolutionHeight(), 
                                                          0.1f, Float.POSITIVE_INFINITY);
            
            for(GLProgram prog : glPrograms.values()) {
                if(prog.containsUniform("uView") && prog.containsUniform("uProjection")) {
                    prog.use();
                    prog.setUniform("uView", false, view);
                    prog.setUniform("uProjection", false, proj);
                }
            }
        }
    }
    
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float easeInOutQuad(float t) {
        return (t < 0.5f) ? 2*t*t : -1+(4-2*t)*t;
    }
    
    boolean transitionComplete() {
        return !transitioning;
    }
    
}