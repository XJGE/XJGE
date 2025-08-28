package org.xjge.test;

import static org.xjge.test.GridSpaceStatus.NONE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridSpace {

    public static final int SIZE = 1;
    
    public final boolean[] unreachableEdge = new boolean[6];
    
    public final int type;
    public final float xLocation;
    public final float yLocation;
    public final float zLocation;
    
    public ComponentUnit occupyingUnit;
    
    public GridSpaceStatus status = NONE;
    
    GridSpace(int type, float xLocation, float yLocation, float zLocation) {
        this.type      = type;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.zLocation = zLocation;
    }
    
}