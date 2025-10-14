package org.xjge.test;

import java.util.Map;
import java.util.Random;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Control;
import org.xjge.core.Input;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.Window;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetManabolt extends Widget {
    
    private boolean gameFinished;
    private boolean timerFinished;
    private boolean blink;
    private boolean onTarget;
    
    private float orbitAngle;
    private float orbitSpeed;
    private float radiusX = 120f;
    private float radiusY;
    private float ovalRotation;
    private final float rotationDriftSpeed;
    
    private int tickCount;
    
    private final Vector2f cursorPos = new Vector2f();
    private final TurnContext turnContext;
    private Texture targetTexture;
    private Texture markerTexture;
    private Icon targetIcon;
    private Icon markerIcon;
    
    private Timer countdown = new Timer();
    private Random random = new Random();
    
    private Rectangle background = new Rectangle(0, 0, 50, 300);
    private Rectangle indicator = new Rectangle(0, 0, 30, 280);
    
    WidgetManabolt(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        targetTexture = new Texture("image_target.png");
        targetIcon = new Icon(targetTexture, 100, 100, true);
        targetIcon.scale.set(1.5f);
        
        markerTexture = new Texture("image_marker.png");
        markerIcon = new Icon(markerTexture, 50, 50, true);
        markerIcon.scale.set(1.5f);
        
        orbitSpeed = (random.nextBoolean()) ? -6f : 6f;
        radiusY = 5f + random.nextFloat(6f);
        rotationDriftSpeed = (random.nextBoolean()) ? -random.nextFloat(2f) : random.nextFloat(2f);
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(!timerFinished) {
            if(countdown.tick(280, 1, false)) {
                indicator.height = countdown.getTime();
                if(countdown.getTime() == 0) timerFinished = true;
            }
            
            float dt = (float) targetDelta / 3f; //TODO: use trueDelta? Switching to fullscreen causes it to speed up
            
            if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
                if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 1)) {
                    cursorPos.x += turnContext.unit.getDeltaCursorX(Control.RIGHT_STICK_X, 1);
                }
                if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 1)) {
                    cursorPos.y += turnContext.unit.getDeltaCursorY(Control.RIGHT_STICK_Y, 1);
                }
            } else {
                if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                    cursorPos.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0) * 4f;
                }
                if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                    cursorPos.y += -(turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0)) * 4f;
                }
            }

            orbitAngle += orbitSpeed * dt; //TODO: lerp orbit speed to change from pos/neg and vice versa?
            if(orbitAngle > Math.PI * 2) orbitAngle -= Math.PI * 2;

            ovalRotation += rotationDriftSpeed * dt;
            if(ovalRotation > Math.PI * 2) ovalRotation -= Math.PI * 2;

            float cosO = (float) Math.cos(ovalRotation);
            float sinO = (float) Math.sin(ovalRotation);

            float cosA = (float) Math.cos(orbitAngle);
            float sinA = (float) Math.sin(orbitAngle);

            markerIcon.position.x = cursorPos.x + cosO * (radiusX * cosA) - sinO * (radiusY * sinA);
            markerIcon.position.y = cursorPos.y + sinO * (radiusX * cosA) + cosO * (radiusY * sinA);
            
            onTarget = markerIcon.position.distance(Window.getResolutionWidth() / 2, Window.getResolutionHeight() / 2) < 75;
            
        } else {
            if(XJGE.tick(3)) {
                blink = !blink;
                tickCount++;
                
                if(tickCount > 15) {
                    gameFinished = true;
                    UI.removeWidget(GLFW_JOYSTICK_1, "manabolt_minigame");
                }
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        background.render(1f, Color.BLACK);
        indicator.render(1f, Color.WHITE);
        
        targetIcon.setColor(onTarget ? Color.WHITE : Color.RED);
        markerIcon.setOpacity((!blink ? 1f : 0.5f));
        
        targetIcon.render();
        markerIcon.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        targetIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        markerIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        
        cursorPos.x = viewportWidth / 2;
        cursorPos.y = viewportHeight / 2;
        
        background.positionX = (viewportWidth / 2) + 200;
        background.positionY = viewportHeight / 2 - background.height / 2;
        
        indicator.positionX = background.positionX + 10;
        indicator.positionY = background.positionY + 10;
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }
    
    boolean isFinished() {
        return gameFinished;
    }
    
    boolean getResult() {
        return onTarget;
    }

}