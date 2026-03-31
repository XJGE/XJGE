package org.xjge.test;

import org.joml.Vector2i;
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
    
    private final double step = 1.0;
    private double value;
    
    private int clickCount;
    private int previousCursorX;
    
    private final Vector2i textPos = new Vector2i();
    private final String type;
    private final ModelAnimator animator;
    
    private final Rectangle buttonUp   = new Rectangle(0, 0, 20, 14);
    private final Rectangle buttonDown = new Rectangle(0, 0, 20, 14);
    
    private final Timer clickTimer = new Timer();
    private final Timer caratTimer = new Timer();
    private final Timer idleTimer  = new Timer();
    
    private final Icon upArrow;
    private final Icon downArrow;
    private Color upArrowColor    = new Color(0.75f);
    private Color downArrowColor  = new Color(0.75f);
    private Color upButtonColor   = new Color(1f);
    private Color downButtonColor = new Color(1f);
    
    public UISpinbox(ModelAnimator animator, int width, Texture iconsTexture, String type) {
        super(width, iconsTexture);
        this.animator = animator;
        this.type     = type;
        
        upArrow   = new Icon(iconsTexture, 24, 24, false, 4, 0);
        downArrow = new Icon(iconsTexture, 24, 24, false, 5, 0);
        
        setValue(animator.getSpeed());
    }
    
    private void setTextToValue() {
        double display = value * step;
        setText(String.format("%.1f", display));
        animator.setSpeed(value);
    }
    
    private void commitText() {
        try {
            var d = Double.parseDouble(typed.toString());
            value = Math.round(d / step);
        } catch(NumberFormatException ignored) {
            setTextToValue();
        }
    }

    @Override
    void update() {
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
        Font.FALLBACK.drawString("Speed", textPos.x, textPos.y, Color.WHITE, 1f);
        
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
        
        buttonUp.render(1f, Color.SILVER);
        buttonDown.render(1f, Color.SILVER);
        
        upArrow.render();
        downArrow.render();
    }

    @Override
    void relocate(Rectangle parent) {
        textPos.x = parent.positionX + 128;
        textPos.y = parent.positionY + parent.height - 32;
        
        bounds.positionX = parent.positionX + 196;
        bounds.positionY = parent.positionY + parent.height - 38;
        
        textPositionX = bounds.positionX + PADDING;
        textPositionY = bounds.positionY + PADDING + 2;
        
        highlight.positionY = bounds.positionY + 2;
        
        buttonUp.positionX = bounds.positionX + bounds.width;
        buttonUp.positionY = bounds.positionY + 14;
        buttonDown.positionX = bounds.positionX + bounds.width;
        buttonDown.positionY = bounds.positionY;
        
        upArrow.position.x   = buttonUp.positionX;
        upArrow.position.y   = buttonUp.positionY - 4;
        downArrow.position.x = buttonDown.positionX;
        downArrow.position.y = buttonDown.positionY - 2;
        
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
                    value++;
                    setTextToValue();
                }
                
                case GLFW_KEY_DOWN -> {
                    value--;
                    setTextToValue();
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
        
        upArrowColor   = (mouse.hovered(buttonUp)) ? Color.WHITE : Color.SILVER;
        downArrowColor = (mouse.hovered(buttonDown)) ? Color.WHITE : Color.SILVER;
        
        if(mouse.clickedOnce(buttonUp, GLFW_MOUSE_BUTTON_LEFT)) {
            value++;
            validate();
        } else if(mouse.clickedOnce(buttonDown, GLFW_MOUSE_BUTTON_LEFT)) {
            value--;
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
    }

    @Override
    void destroy() {
    }
    
    public final void setValue(double val) {
        value = Math.round(val / step);
        setTextToValue();
    }
    
    public double getValue() {
        return value * step;
    }
    
}