package org.xjge.test;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
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
    private final Vector2f gridSpaceOverlap = new Vector2f();
    
    private final Color color; //TODO: this is temporary, we'll use Cyan in prod.
    
    private final List<Vector3i> occupiedGridSpaces = new ArrayList<>();
    
    ComponentAABB(float width, float height, float depth, Color color) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        this.color  = color;
        
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
    
    private boolean fauxOverlap(float position1, float position2, boolean edge) {
        return position1 > position2 && edge;
    }
    
    private List<Integer> addGridSpacesAlongAxis(float position, float minExtent, float maxExtent) {
        int minGridSpace = (int) Math.round(position - minExtent);
        int maxGridSpace = (int) Math.round(position + maxExtent);
        
        var occupiedCells = new ArrayList<Integer>(maxGridSpace - minGridSpace + 1);
        for(int i = minGridSpace; i <= maxGridSpace; i++) occupiedCells.add(i);

        return occupiedCells;
    }
    
    void update(Vector3f position, Map<Vector3i, GridSpace> gridSpaces, Collection<Entity> entities) {
        graphics.modelMatrix.translation(position);
        
        occupiedGridSpaces.clear();
        
        List<Integer> xGridSpaces = addGridSpacesAlongAxis(position.x, width / 2, width / 2);
        List<Integer> yGridSpaces = addGridSpacesAlongAxis(position.y, 0, height);
        List<Integer> zGridSpaces = addGridSpacesAlongAxis(position.z, depth / 2, depth / 2);
        
        xGridSpaces.forEach(x -> {
            yGridSpaces.forEach(y -> {
                zGridSpaces.forEach(z -> {
                    occupiedGridSpaces.add(new Vector3i(x, y, z));
                });
            });
        });

        occupiedGridSpaces.forEach(location -> {
            if(gridSpaces.containsKey(location) && gridSpaces.get(location).type == 1) {
                GridSpace gridSpace = gridSpaces.get(location);
                
                gridSpaceOverlap.x = (position.x < gridSpace.xLocation) 
                                   ? (gridSpace.xLocation - 0.5f) - (position.x + (width / 2))
                                   : (gridSpace.xLocation + 0.5f) - (position.x - (width / 2));
                
                //This is actually for the z-axis check
                gridSpaceOverlap.y = (position.z < gridSpace.zLocation) 
                                   ? (gridSpace.zLocation - 0.5f) - (position.z + (depth / 2))
                                   : (gridSpace.zLocation + 0.5f) - (position.z - (depth / 2));
                
                switch(gridSpaceOverlap.minComponent()) {
                    case 0 -> {
                        if(!(gridSpace.unreachableEdge[0] && gridSpace.unreachableEdge[1])) {
                            if(!fauxOverlap(position.x, gridSpace.xLocation, gridSpace.unreachableEdge[1])) {
                                position.x += gridSpaceOverlap.x;
                            }
                        }
                    }

                    case 1 -> {
                        if(!(gridSpace.unreachableEdge[2] && gridSpace.unreachableEdge[3])) {
                            if(!fauxOverlap(position.z, gridSpace.zLocation, gridSpace.unreachableEdge[2])) {
                                position.z += gridSpaceOverlap.y;
                            }
                        }
                    }
                }
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
        shader.setUniform("uColor", color.asVector3f());
        
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