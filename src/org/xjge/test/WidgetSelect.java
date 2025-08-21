package org.xjge.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.joml.Vector2i;
import org.xjge.core.Mouse;
import org.xjge.core.SplitScreenType;
import org.xjge.core.Window;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.ui.Font;
import org.xjge.ui.Rectangle;
import org.xjge.ui.Widget;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class WidgetSelect extends Widget {

    int choice;
    
    private final ComponentUnit activeUnit;
    private final Vector2i textPos = new Vector2i();
    
    private final List<Option> options = new ArrayList<>();
    
    private class Option {
        final String name;
        final Rectangle background;
        
        Option(String name) {
            this.name       = name;
            this.background = new Rectangle(10, 0, 300, 40);
        }
    }
    
    WidgetSelect(ComponentUnit activeUnit, Map<UUID, ComponentUnit> units) {
        this.activeUnit = activeUnit;
        
        options.add(new Option("Move"));
        options.add(new Option("Use Item"));
        options.add(new Option("Attack"));
        
        relocate(Window.getSplitScreenType(), Window.getResolutionWidth(), Window.getResolutionHeight());
    }
    
    @Override
    public void update(double targetDelta, double trueDelta) {
        if(choice == -1) choice = options.size() - 1;
        if(choice == options.size()) choice = 0;
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms) {
        for(int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            
            option.background.positionY = Window.getResolutionHeight() - (option.background.height * (i + 1)) - (10 * (i + 1));
            
            option.background.render(0.5f, Color.BLACK);
            textPos.set(option.background.positionX + 10, option.background.positionY + 10);
            Font.fallback.drawString(option.name, textPos.x, textPos.y, (choice == i) ? Color.YELLOW : Color.WHITE, 1f);
        }
        
        textPos.set(Window.getResolutionWidth() - Font.fallback.lengthInPixels(activeUnit.name + " turn") - 30,
                    Window.getResolutionHeight() - 40);
        
        Font.fallback.drawString(activeUnit.name + " turn", textPos.x, textPos.y, (activeUnit.team == 0) ? Color.BLUE : Color.RED, 1f);
    }

    @Override
    public final void relocate(SplitScreenType splitType, int viewportWidth, int viewportHeight) {
    }

    @Override
    public void processKeyboardInput(int key, int action, int mods) {
    }

    @Override
    public void processMouseInput(Mouse mouse) {
    }

    @Override
    public void delete() {
    }

    String getSelectedOption() {
        return options.get(choice).name;
    }
    
}