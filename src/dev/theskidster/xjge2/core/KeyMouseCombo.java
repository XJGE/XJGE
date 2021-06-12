package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.input.Control;
import static dev.theskidster.xjge2.input.Control.*;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

final class KeyMouseCombo extends InputDevice {

    int[] axisValues = new int[4];
    
    private float prevAxisX = Window.getWidth() / 2;
    private float prevAxisY = Window.getHeight() / 2;
    
    private boolean firstMouse = true;
    
    private final DoubleBuffer cursorPosX;
    private final DoubleBuffer cursorPosY;
    
    KeyMouseCombo(int id, float sensitivity, HashMap<Control, Integer> config) {
        super(id, sensitivity, config);
        
        name = "KeyMouseCombo";
        
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
            puppets.peek().commands.forEach((control, command) -> {
                switch(control) {
                    case LEFT_STICK_X, LEFT_STICK_Y -> {
                        int key1 = (config.get(control) & ((control == LEFT_STICK_X) ? axisValues[0] : axisValues[2]));
                        int key2 = (config.get(control) & ((control == LEFT_STICK_X) ? axisValues[1] : axisValues[3]));
                        
                        if(glfwGetKey(Window.HANDLE, key1) == GLFW_PRESS) {
                            command.execute(-1, this, control);
                        } else if(glfwGetKey(Window.HANDLE, key2) == GLFW_PRESS) {
                            command.execute(1, this, control);
                        }
                    }
                    
                    case RIGHT_STICK_X ->  {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);

                        if((float) cursorPosX.get(0) != prevAxisX) {
                            command.execute(findAxisValue((float) cursorPosX.get(0), prevAxisX), this, control);
                            prevAxisX = (float) cursorPosX.get(0);
                        }
                    }
                        
                    case RIGHT_STICK_Y -> {
                        glfwGetCursorPos(Window.HANDLE, cursorPosX, cursorPosY);
                        
                        if((float) cursorPosY.get(0) != prevAxisY) {
                            command.execute(findAxisValue((float) cursorPosY.get(0), prevAxisY), this, control);
                            prevAxisY = (float) cursorPosY.get(0);
                        }
                    }
                        
                    case L2, R2 -> command.execute(glfwGetMouseButton(Window.HANDLE, config.get(control)), this, control);
                    
                    default -> command.execute(glfwGetKey(Window.HANDLE, config.get(control)), this, control);
                }
            });
        }
        
        resolvePuppetSetRequest();
    }

}