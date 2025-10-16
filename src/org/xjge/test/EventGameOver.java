package org.xjge.test;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Event;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class EventGameOver extends Event {
    
    private final Timer timer = new Timer();
    
    public EventGameOver(int priority, ComponentUnit unit) {
        super(priority);
        
        String text = unit.isPlayer ? "Red unit wins!" : "Blue unit wins!";
        Color color = unit.isPlayer ? Color.RED : Color.CYAN;
        
        UI.addWidget(GLFW_JOYSTICK_1, "game_over", new WidgetGameOver(text, color));
    }

    @Override
    public void resolve() {
        if(timer.tick(5, 60, false) && timer.getTime() == 0) {
            XJGE.setScene(new Scene3D());
            resolved = true;
        }
    }

}