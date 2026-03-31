package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;
import org.xjge.core.Mouse;
import org.xjge.core.Timer;
import org.xjge.graphics.Color;
import org.xjge.graphics.ModelAnimator;
import org.xjge.graphics.Texture;
import org.xjge.ui.Font;
import org.xjge.ui.Icon;
import org.xjge.ui.Rectangle;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class UISpinbox extends UITextInput {
    
    private boolean startClickTimer;
    
    private double value;
    
    private int clickCount;
    private int previousCursorX;
    
    private final String format = "%.2f";
    private final String type;
    private final ModelAnimator animator;
    
    private final Rectangle buttonUp   = new Rectangle(0, 0, 20, 14);
    private final Rectangle buttonDown = new Rectangle(0, 0, 20, 14);
    
    private final Timer clickTimer = new Timer();
    private final Timer caratTimer = new Timer();
    private final Timer idleTimer  = new Timer();
    
    private final Icon upArrow;
    private final Icon downArrow;
    private Color upArrowColor    = new Color(0.25f);
    private Color downArrowColor  = new Color(0.25f);
    private Color upButtonColor   = new Color(0.75f);
    private Color downButtonColor = new Color(0.75f);
    
    public UISpinbox(ModelAnimator animator, int width, Texture iconsTexture, String type) {
        super(width, iconsTexture);
        this.animator = animator;
        this.type     = type;
        
        upArrow   = new Icon(iconsTexture, 24, 24, false, 4, 0);
        downArrow = new Icon(iconsTexture, 24, 24, false, 5, 0);
        
        switch(type) {
            case "Speed"          -> setValue(animator.getSpeed());
            case "Animation Time" -> setValue(animator.getTime());
        }
    }
    
    private void setTextToValue() {
        setText(String.format(format, value));
    }
    
    private void commitText() {
        try {
            value = Double.parseDouble(typed.toString());
            
            switch(type) {
                case "Speed"          -> animator.setSpeed(value);
                case "Animation Time" -> animator.setTime(value);
            }
        } catch(NumberFormatException ignored) {
            setTextToValue();
        }
    }

    @Override
    void update() {
        switch(type) {
            case "Speed" -> {
                if(!hasFocus()) {
                    value = animator.getSpeed();
                    setText(String.format(format, value));
                }
            }
            case "Animation Time" -> {
                if(hasFocus()) {
                    if(animator.getSpeed() != 0) animator.setSpeed(0);
                } else {
                    value = animator.getTime();
                    setText(String.format(format, value));
                }
            }
        }
        
        if(startClickTimer) {
            clickTimer.tick(18, 1, false);
            
            if(clickTimer.getTime() == 0) {
                caratBlink      = true;
                startClickTimer = false;
                clickTimer.reset();
            } else {
                caratBlink = false;
            }
        }
        
        if(caratIdle) {
            if(idleTimer.tick(2, 18, false) && idleTimer.getTime() == 0) {
                caratBlink = true;
                caratIdle  = false;
            }
        } else {
            if(caratBlink) {
                if(caratTimer.tick(18)) showCarat = !showCarat;
            } else {
                showCarat = hasFocus();
            }
        }
        
        if(clickCount > 0) highlightText(carat.position.x);
    }

    @Override
    void render() {
        bounds.render(1f, Color.GRAY);
        
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) bounds.positionX, (int) bounds.positionY, (int) bounds.width, (int) bounds.height);
            highlight.render(1f, Color.BLUE);
            
            Font.FALLBACK.drawString(typed.toString(), 
                            textPositionX + getTextScrollOffset(), 
                            textPositionY, 
                            Color.WHITE, 1f);
            
            if(showCarat && !disabled) carat.render();
        glDisable(GL_SCISSOR_TEST);
        
        buttonUp.render(1f, upButtonColor);
        buttonDown.render(1f, downButtonColor);
        
        upArrow.setColor(upArrowColor);
        downArrow.setColor(downArrowColor);
        
        upArrow.render();
        downArrow.render();
    }

    @Override
    void relocate(Rectangle parent, int offsetX, int offsetY) {
        bounds.positionX = parent.positionX + offsetX;
        bounds.positionY = parent.positionY + parent.height - offsetY;
        
        textPositionX = bounds.positionX + PADDING;
        textPositionY = bounds.positionY + PADDING + 2;
        
        highlight.positionY = bounds.positionY + 2;
        
        buttonUp.positionX = bounds.positionX + bounds.width;
        buttonUp.positionY = bounds.positionY + 14;
        buttonDown.positionX = bounds.positionX + bounds.width;
        buttonDown.positionY = bounds.positionY;
        
        upArrow.position.x   = buttonUp.positionX - 2;
        upArrow.position.y   = buttonUp.positionY - 4;
        downArrow.position.x = buttonDown.positionX - 2;
        downArrow.position.y = buttonDown.positionY - 4;
        
        unfocus();
    }

    @Override
    String processKeyInput(int key, int action, int mods) {
        if(disabled || !hasFocus()) return null;
        
        boolean shiftHeld = mods == GLFW_MOD_SHIFT;
        boolean ctrlHeld  = mods == GLFW_MOD_CONTROL;
        
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            caratBlink = false;
            caratIdle  = true;
            showCarat  = true;
            idleTimer.reset();
            
            if(!ctrlHeld) {
                keyChars.forEach((k, c) -> {
                    if(key == k) {
                        if(highlight.width > 0) {
                            int min = Math.min(firstIndex, lastIndex);
                            int max = Math.max(firstIndex, lastIndex);

                            typed.replace(min, max, "");

                            setIndex(min);
                            scrollText();

                            highlight.width = 0;
                        }

                        insertChar(c.getChar(shiftHeld));
                    }
                });
            } else {
                switch(key) {
                    case GLFW_KEY_A -> {
                        if(typed.length() > 0) {
                            setIndex(0);
                            firstIndex    = 0;
                            firstIndexSet = true;
                            highlightText(textPositionX + Font.FALLBACK.lengthInPixels(typed.toString()));
                        }
                    }
                }
            }
            
            switch(key) {
                case GLFW_KEY_BACKSPACE -> {
                    if(getIndex() > 0) {
                        if(highlight.width > 0) {
                            deleteSection();
                        } else {
                            setIndex(getIndex() - 1);
                            typed.deleteCharAt(getIndex());
                            scrollText();
                        }
                    } else {
                        if(highlight.width > 0) deleteSection();
                    }
                }
                    
                case GLFW_KEY_RIGHT -> {
                    if(highlight.width > 0) {
                        setIndex(Math.max(firstIndex, lastIndex));
                        scrollText();
                        highlight.width = 0;
                    } else {
                        setIndex((getIndex() > typed.length() - 1) ? typed.length() : getIndex() + 1);
                    }
                    
                    scrollText();
                }
                    
                case GLFW_KEY_LEFT -> {
                    if(highlight.width > 0) {
                        setIndex(Math.min(firstIndex, lastIndex));
                        scrollText();
                        highlight.width = 0;
                    } else {
                        setIndex((getIndex() <= 0) ? 0 : getIndex() - 1);
                    }
                    
                    scrollText();
                }
                
                case GLFW_KEY_UP -> {
                    value += 0.01;
                    validate();
                }
                
                case GLFW_KEY_DOWN -> {
                    value -= 0.01;
                    validate();
                }
                
                case GLFW_KEY_ENTER -> {
                    commitText();
                    unfocus();
                    return value + "";
                }
            }
        }
        
        return null;
    }

    @Override
    String processMouseInput(Mouse mouse) {
        if(disabled) return null;
        
        if(mouse.hovered(buttonUp)) {
            upButtonColor.set(1f);
            upArrowColor.set(0.5f);
        } else {
            upButtonColor.set(0.75f);
            upArrowColor.set(0.25f);
        }
        
        if(mouse.hovered(buttonDown)) {
            downButtonColor.set(1f);
            downArrowColor.set(0.5f);
        } else {
            downButtonColor.set(0.75f);
            downArrowColor.set(0.25f);
        }
        
        if(mouse.clickedOnce(buttonUp, GLFW_MOUSE_BUTTON_LEFT)) {
            value += 0.1;
            validate();
        } else if(mouse.clickedOnce(buttonDown, GLFW_MOUSE_BUTTON_LEFT)) {
            value -= 0.1;
            validate();
        }
        
        if(mouse.clickedOnce(bounds, GLFW_MOUSE_BUTTON_LEFT)) {
            clickCount      = (startClickTimer) ? clickCount + 1 : 0;
            startClickTimer = true;

            int leftIndex = 0;

            if(clickCount > 0 && clickTimer.getTime() != 0 && hasFocus()) {
                String[] words = typed.toString().split("\\s|/|-|\\\\");
                int rightIndex = 0;

                for(String word : words) {
                    leftIndex  = typed.toString().indexOf(word);
                    rightIndex = leftIndex + word.length() - 1;

                    if(getIndex() >= leftIndex && getIndex() <= rightIndex) {
                        break;
                    }
                }

                setIndex(rightIndex + 1);
                scrollText();
            }

            clickTimer.reset();

            if(!hasFocus()) {
                focus();
                previousCursorX = (int) mouse.getCursorPositionX();
                scrollText();
            }

            if(typed.length() > 0) {
                if(clickCount == 0) {
                    int newIndex = findClosestIndex(((float) mouse.getCursorPositionX()) - bounds.positionX - PADDING);
                    setIndex(newIndex);
                    firstIndex = getIndex();
                    highlight.width = 0;
                } else {
                    firstIndex = leftIndex;
                }

                firstIndexSet = true;
            }
            
            scrollText();
            
        } else {
            if(!mouse.hovered(bounds) && mouse.clickedOnce() && hasFocus()) {
                commitText();
                unfocus();
                return value + "";
            }
            
            if(clickCount == 0 && hasFocus()) {
                if(mouse.getCursorPositionX() != previousCursorX && mouse.clicked()) {
                    highlightText((float) mouse.getCursorPositionX());
                }
                
                caratBlink = !mouse.clicked();
            }
        }
        
        return null;
    }

    @Override
    void validate() {
        setTextToValue();
        commitText();
    }

    @Override
    void destroy() {
    }
    
    public final void setValue(double val) {
        value = val;
        setTextToValue();
        commitText();
    }
    
    public double getValue() {
        return value;
    }
    
}