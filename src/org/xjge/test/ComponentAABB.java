package org.xjge.test;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL30C.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.Component;
import org.xjge.core.Entity;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentAABB extends Component {

    float width;
    float height;
    float depth;
    
    private final Graphics graphics = new Graphics();
    
    private final List<Vector3i> occupiedGridSpaces = new ArrayList<>();
    
    ComponentAABB(float width, float height, float depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            int[] indexData = {
                //Front
                4,5,
                5,6,
                6,7,
                7,4,
                
                //Back
                0,1,
                1,2,
                2,3,
                3,0,
                
                //Center
                0,4,
                3,7,
                1,5,
                2,6
            };
            
            graphics.vertices = stack.mallocFloat(24);
            graphics.indices  = stack.mallocInt(indexData.length);
            
            graphics.indices.put(indexData).flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
    }
    
    private List<Integer> addGridSpacesAlongAxis(float position, float size1, float size2) {
        int minGridSpace = Math.round(position - size1 - 0.5f);
        int maxGridSpace = Math.round(position + size2 - 0.5f);
        int spaceBetween = maxGridSpace - minGridSpace;
        
        var occupiedCellsAlongAxis = new ArrayList<Integer>();
        
        if(minGridSpace == maxGridSpace) {
            occupiedCellsAlongAxis.add(maxGridSpace);
        } else {
            occupiedCellsAlongAxis.add(minGridSpace);
            
            for(int c = 0; c <= spaceBetween; c++) {
                if((minGridSpace + c != maxGridSpace) && (minGridSpace + c != minGridSpace)) {
                    occupiedCellsAlongAxis.add(minGridSpace + c);
                }
            }
            
            occupiedCellsAlongAxis.add(maxGridSpace);
        }
        
        return occupiedCellsAlongAxis;
    }
    
    void update(Vector3f position, Map<Vector3i, GridSpace> gridSpaces, Collection<Entity> entities) {
        graphics.modelMatrix.translation(position);
        
        occupiedGridSpaces.clear();
        
        List<Integer> xGridSpaces = addGridSpacesAlongAxis(position.x, (width / 2), (width / 2));
        List<Integer> yGridSpaces = addGridSpacesAlongAxis(position.y, 0, height);
        List<Integer> zGridSpaces = addGridSpacesAlongAxis(position.z, (depth / 2), (depth / 2));
        
        xGridSpaces.forEach(x -> {
            yGridSpaces.forEach(y -> {
                zGridSpaces.forEach(z -> {
                    occupiedGridSpaces.add(new Vector3i(x, y, z));
                });
            });
        });
        
        entities.forEach(entity -> {
            if(entity.hasComponent(this.getClass()) && gridSpaces != null) {
                occupiedGridSpaces.forEach(location -> {
                    if(gridSpaces.containsKey(location)) {
                        System.out.println("(" + location.x + ", " + location.y + ", " + location.z + ") " +
                                           gridSpaces.get(location).type);
                    }
                    //if(gridSpaces.get(location) != null) resolveCollision(gridSpaces.get(location));
                    
                    /*
                    if(!entity.equals(this) && ((AABBEntity) entity).occupiedCells.contains(location)) {
                        resolveCollision(entity);
                    }
                    */
                });
            }
        });
    }
    
    void render(GLProgram shader) {
        shader.use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(4);
        
        glBindVertexArray(graphics.vao);
        
        shader.setUniform("uModel", false, graphics.modelMatrix);
        shader.setUniform("uColor", Color.CYAN.asVector3f());
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(24);
            
            float halfWidth = width / 2;
            float halfDepth = depth / 2;
            
            vertices.put(-halfWidth).put(0)      .put(halfDepth);
            vertices .put(halfWidth).put(0)      .put(halfDepth);
            vertices .put(halfWidth).put(height) .put(halfDepth);
            vertices.put(-halfWidth).put(height) .put(halfDepth);
            vertices.put(-halfWidth).put(0)     .put(-halfDepth);
            vertices .put(halfWidth).put(0)     .put(-halfDepth);
            vertices .put(halfWidth).put(height).put(-halfDepth);
            vertices.put(-halfWidth).put(height).put(-halfDepth);
            
            vertices.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, graphics.vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        
        glDrawElements(GL_LINES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glLineWidth(1);
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
}