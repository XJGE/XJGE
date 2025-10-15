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
class WidgetCrystalBall extends Widget {

    private boolean spinning;
    
    private final int maxMomentum = 70;
    private int momentum;
    
    private Vector2f prevCursorPos = new Vector2f();
    
    private final TurnContext turnContext;
    private final Icon ballIcon = new Icon(new Texture("image_crystalball.png"), 100, 100, true);
    private final Icon wandIcon = new Icon(new Texture("image_wand.png"), 100, 20, false);
    private final Icon[] numbers = new Icon[6];
    
    WidgetCrystalBall(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        ballIcon.scale.set(2f);
        wandIcon.scale.set(1.5f);
        wandIcon.rotation.z = -150f;
        
        Texture numberTexture = new Texture("image_numbers.png");
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = new Icon(numberTexture, 30, 30, true);
            
            int x = (i > 2) ? i - 3 : i;
            int y = (i > 2) ? 1 : 0;
            
            numbers[i].setSubImage(x, y);
            numbers[i].scale.set(2.5f);
        }
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        prevCursorPos.set(wandIcon.position);
        
        if(turnContext.unit.inputDeviceID == Input.KEYBOARD) {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 1)) {
                wandIcon.position.x += turnContext.unit.getDeltaCursorX(Control.RIGHT_STICK_X, 1);
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 1)) {
                wandIcon.position.y += turnContext.unit.getDeltaCursorY(Control.RIGHT_STICK_X, 1);
            }
        } else {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                wandIcon.position.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0) * 4f;
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                wandIcon.position.y += -(turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0)) * 4f;
            }
        }
        
        if(momentum < maxMomentum) {
            momentum += Math.abs(prevCursorPos.x - wandIcon.position.x) * 0.02f;
            momentum += Math.abs(prevCursorPos.y - wandIcon.position.y) * 0.02f;
            if(momentum >= maxMomentum) spinning = true;
        }
        
        /*
        1. Wave wand to "charge" the crystal ball, numbers will start spinning
        2. Once the ball is fully charged the numbers will continue to cycle (carousel effect)
        3. Press the action button to select a number (light RNG)
        */
        
        float speed  = (spinning) ? maxMomentum : momentum;
        float center = Window.getResolutionWidth() / 2f;

        for(Icon number : numbers) {
            number.position.x -= speed * 0.2f;
            if(number.position.x < center - (90 * number.scale.x)) {
                number.position.x = center + (90 * number.scale.x);
            }
            
            float maxDistance = 30f * number.scale.x;
            float distance = Math.abs(number.position.x - center);

            float normalized = 1f - Math.min(distance / maxDistance, 1f);
            number.setOpacity(normalized);
        }

        if(!spinning) {
            if(!turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0) &&
               !turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                if(momentum > 0 && XJGE.tick(5)) momentum--;
            }

            if(momentum > maxMomentum) momentum = maxMomentum;
            if(momentum <= 0) momentum = 0;
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        ballIcon.render();
        for(Icon number : numbers) number.render();
        wandIcon.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        ballIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        wandIcon.position.set(ballIcon.position.x + 200, ballIcon.position.y + 100);
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i].position.set(ballIcon.position.x + (i * (30 * numbers[i].scale.x)), ballIcon.position.y + 10);
        }
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }

}