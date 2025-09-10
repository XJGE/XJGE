package org.xjge.test;

import static org.xjge.test.GridSpaceStatus.NONE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridSpace {

    public static final int SIZE = 1;
    
    public final boolean[] unreachableEdge = new boolean[4];
    
    public final int type;
    public final int xLocation;
    public final int yLocation;
    public final int zLocation;
    
    public AttributeUnit occupyingUnit;
    public GridSpace previousSpace;
    
    public GridSpaceStatus status = NONE;
    
    GridSpace(int type, int xLocation, int yLocation, int zLocation) {
        this.type      = type;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.zLocation = zLocation;
    }
    
}