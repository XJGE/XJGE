package org.xjge.core;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class DGSystemInfo extends DebugGroup {
    
    DGSystemInfo(String title) {
        super(title);
        for(int i = 0; i < 6; i++) output.add(new StringBuilder());
    }
    
    @Override
    protected void update(double deltaMetric, int fps, int entityCount) {
        for(int i = 0; i < output.size(); i++) {
            output.get(i).setLength(0);
            
            switch(i) {
                case 0 -> output.get(i).append("OS NAME:    ").append(System.getProperty("os.name"));
                case 1 -> output.get(i).append("JAVA VER:   ").append(System.getProperty("java.version"));
                case 2 -> output.get(i).append("XJGE VER:   ").append(XJGE.VERSION);
                case 3 -> output.get(i).append("GLFW VER:   ").append(glfwGetVersionString());
                case 4 -> output.get(i).append("OPENAL VER: ").append(alGetString(AL_VERSION));
                case 5 -> output.get(i).append("OPENGL VER: ").append(glGetString(GL_VERSION));
            }
        }
    }

    @Override
    protected void render() {}

}