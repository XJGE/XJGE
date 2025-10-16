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
    private boolean stop;
    
    private final float deceleration = 0.5f;
    
    private final int maxMomentum = 70;
    private int momentum;
    
    private Number landedNumber = null;
    private final Vector2f prevCursorPos = new Vector2f();
    private final TurnContext turnContext;
    private final Icon ballIcon = new Icon(new Texture("image_crystalball.png"), 100, 100, true);
    private final Icon wandIcon = new Icon(new Texture("image_wand.png"), 100, 20, false);
    private final Number[] numbers = new Number[6];
    
    private class Number { 
        final int value;
        final Icon icon;
        
        Number(int value, Icon icon) {
            this.value = value;
            this.icon  = icon;
        }
    }
    
    WidgetCrystalBall(TurnContext turnContext) {
        this.turnContext = turnContext;
        
        ballIcon.scale.set(2f);
        wandIcon.scale.set(1.5f);
        wandIcon.rotation.z = -150f;
        
        Texture numberTexture = new Texture("image_numbers.png");
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = new Number(i + 1, new Icon(numberTexture, 30, 30, true));
            
            int x = (i > 2) ? i - 3 : i;
            int y = (i > 2) ? 1 : 0;
            
            numbers[i].icon.setSubImage(x, y);
            numbers[i].icon.scale.set(2.5f);
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
            if(spinning && !stop && turnContext.unit.buttonPressed(Control.L2, 1)) {
                stop = true;
            }
        } else {
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_X, 0)) {
                wandIcon.position.x += turnContext.unit.getInputValue(Control.RIGHT_STICK_X, 0) * 4f;
            }
            if(turnContext.unit.axisMoved(Control.RIGHT_STICK_Y, 0)) {
                wandIcon.position.y += -(turnContext.unit.getInputValue(Control.RIGHT_STICK_Y, 0)) * 4f;
            }
        }
        
        if(landedNumber != null) return;
        
        if(momentum < maxMomentum && !stop) {
            momentum += Math.abs(prevCursorPos.x - wandIcon.position.x) * 0.02f;
            momentum += Math.abs(prevCursorPos.y - wandIcon.position.y) * 0.02f;
            if(momentum >= maxMomentum) spinning = true;
        }
        
        float center = Window.getResolutionWidth() / 2f;
        
        if(spinning && stop) {
            if(momentum > 0) {
                momentum -= deceleration;
            } else {
                momentum = 0;
                spinning = false;
                float closestDistance = Float.MAX_VALUE;
                Number closest = null;
                
                for(Number number : numbers) {
                    float distance = Math.abs(number.icon.position.x - center);
                    if(distance < closestDistance) {
                        closestDistance = distance;
                        closest = number;
                    }
                }
                
                landedNumber = closest;
                
                if(landedNumber != null) {
                    float offset = center - landedNumber.icon.position.x;
                    for(Number number : numbers) number.icon.position.x += offset;
                }
            }
        }

        for(Number number : numbers) {
            number.icon.position.x -= momentum * 0.2f;
            if(number.icon.position.x < center - (90 * number.icon.scale.x)) {
                number.icon.position.x = center + (90 * number.icon.scale.x);
            }
            
            float maxDistance = 30f * number.icon.scale.x;
            float distance = Math.abs(number.icon.position.x - center);
            
            float normalized = 1f - Math.min(distance / maxDistance, 1f);
            number.icon.setOpacity(normalized);
        }

        if(!spinning && !stop) {
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
        for(Number number : numbers) number.icon.render();
        wandIcon.render();
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
        ballIcon.position.set(viewportWidth / 2, viewportHeight / 2);
        wandIcon.position.set(ballIcon.position.x + 200, ballIcon.position.y + 100);
        
        for(int i = 0; i < numbers.length; i++) {
            numbers[i].icon.position.set(ballIcon.position.x + (i * (30 * numbers[i].icon.scale.x)), ballIcon.position.y + 10);
        }
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {}

    @Override
    public void processMouseInput(Mouse mouse) {}

    @Override
    public void delete() {
    }
    
    boolean finished() {
        return landedNumber != null;
    }
    
    int result() {
        return landedNumber.value;
    }

}