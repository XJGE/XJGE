package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.XJGE;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 15, 2021
 */

public class Polygon {

    private final int numSides;
    public boolean fill;
    public Color color;
    private final Graphics g = new Graphics();
    
    public Polygon(int numSides, boolean fill, float radius, Color color, int xPos, int yPos) {
        this.numSides = numSides;
        this.fill     = fill;
        this.color    = color;
        
        float doublePI = (float) (Math.PI * 2f);
        float[] vertX  = new float[this.numSides];
        float[] vertY  = new float[this.numSides];
        
        for(int v = 0; v < this.numSides; v++) {
            vertX[v] = (float) (radius * Math.cos(v * doublePI / this.numSides));
            vertY[v] = (float) (radius * Math.sin(v * doublePI / this.numSides));
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(this.numSides * 3);
            for(int v = 0; v < this.numSides; v++) g.vertices.put(vertX[v]).put(vertY[v]).put(-100);
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        translate(xPos, yPos);
    }
    
    public Polygon(int numSides, boolean fill, float radius, Color color, Vector2i position) {
        this(numSides, fill, radius, color, position.x, position.y);
    }
    
    public final void translate(Vector2i position) {
        g.modelMatrix.translation(position.x, position.y, 0);
    }
    
    public final void translate(int x, int y) {
        g.modelMatrix.translation(x, y, 0);
    }
    
    public final void rotate(float angle) {
        g.modelMatrix.rotateZ((float) Math.toRadians(angle * -1f));
    }
    
    public void render() {
        XJGE.getDefaultGLProgram().use();
        
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 2);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uColor", color.asVec3());
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, numSides);
        
        ErrorUtils.checkGLError();
    }
    
    public void freeBuffers() {
        g.freeBuffers();
    }
    
}