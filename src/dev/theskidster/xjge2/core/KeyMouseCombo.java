package dev.theskidster.xjge2.core;

import static dev.theskidster.xjge2.core.Control.*;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

final class KeyMouseCombo extends InputDevice {

    private float prevAxisX = Window.getWidth() / 2;
    private float prevAxisY = Window.getHeight() / 2;
    
    private boolean firstMouse = true;
    
    private final DoubleBuffer cursorPosX;
    private final DoubleBuffer cursorPosY;
    
    private final HashMap<Control, Integer> keys = new HashMap<>();
    private final HashMap<Control, Integer> mouse = new HashMap<>();
    private final HashMap<Control, AxisButtons> axes = new HashMap<>();
    
    private final class AxisButtons {
        final int button1;
        final int button2;
        
        AxisButtons(int button1, int button2) {
            this.button1 = button1;
            this.button2 = button2;
        }
    }
    
    KeyMouseCombo(int id) {
        super(id);
        
        name = "KeyMouseCombo";
        
        //TODO: use importControlConfig()
        keys.put(CROSS,      GLFW_KEY_SPACE);
        keys.put(CIRCLE,     GLFW_KEY_Q);
        keys.put(SQUARE,     GLFW_KEY_R);
        keys.put(TRIANGLE,   GLFW_KEY_E);
        keys.put(L1,         GLFW_KEY_1);
        keys.put(R1,         GLFW_KEY_2);
        keys.put(SHARE,      GLFW_KEY_TAB);
        keys.put(OPTIONS,    GLFW_KEY_ESCAPE);
        keys.put(PS_BUTTON,  GLFW_KEY_ENTER);
        keys.put(L3,         GLFW_KEY_LEFT_SHIFT);
        keys.put(R3,         GLFW_KEY_C);
        keys.put(DPAD_UP,    GLFW_KEY_UP);
        keys.put(DPAD_RIGHT, GLFW_KEY_RIGHT);
        keys.put(DPAD_DOWN,  GLFW_KEY_DOWN);
        keys.put(DPAD_LEFT,  GLFW_KEY_LEFT);
        
        axes.put(LEFT_STICK_X,   new AxisButtons(GLFW_KEY_A, GLFW_KEY_D));
        axes.put(LEFT_STICK_Y,   new AxisButtons(GLFW_KEY_W, GLFW_KEY_S));
        mouse.put(RIGHT_STICK_X, 0);
        mouse.put(RIGHT_STICK_Y, 0);
        mouse.put(L2,            GLFW_MOUSE_BUTTON_LEFT);
        mouse.put(R2,            GLFW_MOUSE_BUTTON_RIGHT);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            cursorPosX = stack.mallocDouble(1);
            cursorPosY = stack.mallocDouble(1);
        }
    }
    
    private float findAxisValue(float currValue, float prevValue) {
        if(firstMouse) {
            prevValue  = currValue;
            firstMouse = false;
        }
        
        return (currValue - prevValue) * sensitivity;
    }
    
    @Override
    protected void poll() {
        if(!puppets.empty() && puppets.peek() != null) {
            puppets.peek().commands.forEach((widget, command) -> {
                switch(widget) {
                    case LEFT_STICK_X, LEFT_STICK_Y -> {
                        if(glfwGetKey(Window.HANDLE, axes.get(widget).button1) == GLFW_PRESS) {
                            command.execute(-1, this);
                        } else if(glfwGetKey(Window.HANDLE, axes.get(widget).button2) == GLFW_PRESS) {
                            command.execute(1, this);
                        }
                    }
                    
                    case RIGHT_STICK_X ->  {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);

                        if((float) cursorPosX.get(0) != prevAxisX) {
                            command.execute(findAxisValue((float) cursorPosX.get(0), prevAxisX), this);
                            prevAxisX = (float) cursorPosX.get(0);
                        }
                    }
                        
                    case RIGHT_STICK_Y -> {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);
                        
                        if((float) cursorPosY.get(0) != prevAxisY) {
                            command.execute(findAxisValue((float) cursorPosY.get(0), prevAxisY), this);
                            prevAxisY = (float) cursorPosY.get(0);
                        }
                    }
                        
                    case L2, R2 -> command.execute(glfwGetMouseButton(Window.HANDLE, mouse.get(widget)), this);
                    
                    default -> command.execute(glfwGetKey(Window.HANDLE, keys.get(widget)), this);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }
    
    @Override
    protected void importControlConfig(String filename) {
        //TODO: import XML and map controls
    }

    @Override
    protected void exportControlConfig() {
    }

}