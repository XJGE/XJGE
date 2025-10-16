package org.xjge.test;

import java.util.Map;
import java.util.UUID;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Timer;
import org.xjge.core.UI;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Font;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetDamage extends Widget {
    
    private final int side;
    
    private final float gravity = 0.4f;
    private float xOffset;
    private float yOffset;
    
    private int tickCount;
    
    private final String text;
    private final Color textColor;
    private final Timer timer = new Timer();
    private final Vector2f velocity = new Vector2f(0, 10);
    final UUID uuid = UUID.randomUUID();

    WidgetDamage(int side, String text, Color textColor) {
        this.side = side;
        this.text = text;
        this.textColor = textColor;
        velocity.x = (side == 1) ? 6 : -6;
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(timer.tick(5)) {
            if(tickCount > 10) UI.removeWidget(GLFW_JOYSTICK_1, "damage_" + uuid);
            tickCount++;
        }
        
        if(velocity.x > targetDelta)       velocity.x -= targetDelta;
        else if(velocity.x < -targetDelta) velocity.x += targetDelta;
        else velocity.x = 0;
        
        velocity.y -= gravity;
        
        xOffset += velocity.x;
        yOffset += velocity.y;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        int halfWidth = Font.fallback.lengthInPixels(text) / 2;
        Font.fallback.drawString(text, 
                                 ((Window.getResolutionWidth() / 2) - halfWidth) + (int)xOffset, 
                                 (Window.getResolutionHeight() - 300) + (int)yOffset, 
                                 textColor, 1f);
    }

    @Override
    public void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods, Character character) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

}