package org.xjge.core;

import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class DGHardware extends DebugGroup {

    public DGHardware(String title) {
        super(title);
        for(int i = 0; i < 4; i++) output.add(new StringBuilder());
    }
    
    @Override
    protected void update(double deltaMetric, int fps, int entityCount) {
        for(int i = 0; i < output.size(); i++) {
            output.get(i).setLength(0);
            
            switch(i) {
                case 0 -> output.get(i).append("CPU:     ").append(Hardware2.getCPUInfo());
                case 1 -> output.get(i).append("GPU:     ").append(glGetString(GL_RENDERER));
                case 2 -> {
                    output.get(i).append("MONITOR: ").append(Window.getMonitor().name)
                                 .append("\" (").append(Window.getMonitor().getInfo()).append(")");
                }
                case 3 -> output.get(i).append("SPEAKER: ").append(Audio.speaker.name);
            }
        }
    }

    @Override
    protected void render() {
    }

}