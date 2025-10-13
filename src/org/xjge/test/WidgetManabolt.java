package org.xjge.test;

import java.util.Map;
import org.joml.Vector2f;
import org.xjge.core.Control;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetManabolt extends Widget {
    
    private float orbitAngle = 0f;
    private float orbitSpeed = 6f;
    private float radiusX = 120f;
    private float radiusY = 5f;
    private float ovalRotation = 0f;
    private final float rotationDriftSpeed;
    
    private final Vector2f cursorPos = new Vector2f();
    private final TurnContext turnContext;
    private Texture targetTexture;
    private Texture markerTexture;
    private Icon targetIcon;
    private Icon markerIcon;
    
    WidgetManabolt(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        targetTexture = new Texture("image_target.png");
        targetIcon = new Icon(targetTexture, 100, 100, true);
        targetIcon.scale.set(1.5f);
        
        markerTexture = new Texture("image_marker.png");
        markerIcon = new Icon(markerTexture, 50, 50, true);
        markerIcon.scale.set(1.5f);
        
        rotationDriftSpeed = (float) ((Math.random() > 0.5) ? -Math.random() : Math.random());
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        float dt = (float) targetDelta / 3f; //TODO: use trueDelta? Switching to fullscreen causes it to speed up
        
        if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 1)) {
                cursorPos.x = turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 1);
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 1)) {
                cursorPos.y = turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 1);
            }
        } else {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                cursorPos.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0) * 4f;
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                cursorPos.y += -(turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0)) * 4f;
            }
        }
        
        orbitAngle += orbitSpeed * dt;
        if(orbitAngle > Math.PI * 2) orbitAngle -= Math.PI * 2;
        
        ovalRotation += rotationDriftSpeed * dt;
        if(ovalRotation > Math.PI * 2) ovalRotation -= Math.PI * 2;

        float cosO = (float) Math.cos(ovalRotation);
        float sinO = (float) Math.sin(ovalRotation);

        float cosA = (float) Math.cos(orbitAngle);
        float sinA = (float) Math.sin(orbitAngle);

        markerIcon.position.x = cursorPos.x + cosO * (radiusX * cosA) - sinO * (radiusY * sinA);
        markerIcon.position.y = cursorPos.y + sinO * (radiusX * cosA) + cosO * (radiusY * sinA);
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        targetIcon.render();
        markerIcon.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        targetIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        markerIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        cursorPos.x = viewportWidth / 2;
        cursorPos.y = viewportHeight / 2;
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }

}