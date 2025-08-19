package org.xjge.core;

import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.glGetString;
import org.xjge.graphics.Texture;
import org.xjge.ui.Icon;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class EMHardware extends EngineMetricsGroup {

    private final Icon[] icons = new Icon[4];
    
    public EMHardware(String title, Texture engineIcons) {
        super(title);
        for(int i = 0; i < 8; i++) output.add(new StringBuilder());
        
        for(int i = 0; i < 4; i++) {
            icons[i] = new Icon(engineIcons, 64, 64, false);
            icons[i].scale.set(0.6f);
        }
    }
    
    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        for(int i = 0; i < output.size(); i++) {
            output.get(i).setLength(0);
            
            switch(i) {
                case 0 -> output.get(i).append("CPU MODEL: ").append(XJGE.getCPUModel());
                case 1 -> output.get(i).append("GPU MODEL: ").append(glGetString(GL_RENDERER));
                case 2 -> {
                    output.get(i).append("MONITOR:   \"").append(Window.getMonitor().name)
                                 .append("\" (").append(Window.getMonitor().getInfo()).append(")");
                }
                case 3 -> output.get(i).append("SPEAKER:   ").append(Audio2.getSpeaker().name);
                case 4, 5, 6, 7 -> {
                    output.get(i).append("    ").append(i - 4).append(": ");
                    if(Input.getDevicePresent(i - 4)) output.get(i).append(Input.getDeviceName(i - 4));
                }
            }
        }
        
        for(int i = 0; i < icons.length; i++) {
            icons[i].position.x = contentArea.positionX + 10;
            icons[i].position.y = (contentArea.positionY - (i * 26)) + 82;
        }
    }

    @Override
    protected void render() {
        for(int i = 0; i < icons.length; i++) {
            if(i != icons.length - 1) {
                if(Input.getDevicePresent(i)) {
                    if(Input.getDeviceEnabled(i)) icons[i].setSubImage(2, 0);
                    else                          icons[i].setSubImage(1, 0);
                } else {
                    icons[i].setSubImage(0, 0);
                }
            }
            
            icons[i].render();
        }
    }

}