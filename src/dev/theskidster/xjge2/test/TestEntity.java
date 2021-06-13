package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Camera;
import static dev.theskidster.xjge2.core.Control.*;
import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Puppet;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.core.Entity;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 3, 2021
 */

public class TestEntity extends Entity {

    Puppet puppet = new Puppet(this);
    Graphics g    = new Graphics();
    
    TestEntity(float x, float y, float z) {
        super(new Vector3f(x, y, z));
        
        puppet.commands.put(CROSS, new LogControlName("Cross"));
        puppet.commands.put(CIRCLE, new LogControlName("Circle"));
        puppet.commands.put(SQUARE, new LogControlName("Square"));
        puppet.commands.put(TRIANGLE, new LogControlName("Triangle"));
        puppet.commands.put(L1, new LogControlName("L1"));
        puppet.commands.put(R1, new LogControlName("R1"));
        puppet.commands.put(SHARE, new LogControlName("Share"));
        puppet.commands.put(OPTIONS, new LogControlName("Options"));
        puppet.commands.put(PS_BUTTON, new LogControlName("PS button"));
        puppet.commands.put(L3, new LogControlName("L3"));
        puppet.commands.put(R3, new LogControlName("R3"));
        puppet.commands.put(DPAD_UP, new LogControlName("D-Pad up"));
        puppet.commands.put(DPAD_DOWN, new LogControlName("D-Pad down"));
        puppet.commands.put(DPAD_LEFT, new LogControlName("D-Pad left"));
        puppet.commands.put(DPAD_RIGHT, new LogControlName("D-Pad right"));
        puppet.commands.put(RIGHT_STICK_X, new LogControlName("Right stick x"));
        puppet.commands.put(RIGHT_STICK_Y, new LogControlName("Right stick y"));
        puppet.commands.put(LEFT_STICK_X, new LogControlName("Left stick x"));
        puppet.commands.put(LEFT_STICK_Y, new LogControlName("Left stick y"));
        puppet.commands.put(L2, new LogControlName("L2"));
        puppet.commands.put(R2, new LogControlName("R2"));
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(18);
            
            //(vec3 position), (vec3 color)
            g.vertices.put(-0.5f).put(-0.5f).put(0) .put(1).put(0).put(0);
            g.vertices    .put(0) .put(0.5f).put(0) .put(0).put(1).put(0);
            g.vertices .put(0.5f).put(-0.5f).put(0) .put(0).put(0).put(1);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(double targetDelta) {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram glProgram, Camera camera) {
        glProgram.use();
        
        glBindVertexArray(g.vao);
        
        glProgram.setUniform("uType", 2);
        glProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        
        ErrorUtils.checkGLError();
    }
    
    @Override
    public void render(Map<String, GLProgram> glPrograms, Camera camera) {}

    @Override
    protected void destroy() {
    }
    
}