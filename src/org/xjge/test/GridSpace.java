package org.xjge.test;

import static org.xjge.test.GridSpaceStatus.NO_STATUS;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridSpace {

    public final int type;
    public final int xLocation;
    public final int yLocation;
    public final int zLocation;
    
    public ComponentUnit occupyingUnit;
    public GridSpace previousSpace;
    
    public GridSpaceStatus status = NO_STATUS;
    
    GridSpace(int type, int xLocation, int yLocation, int zLocation) {
        this.type      = type;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.zLocation = zLocation;
    }
    
}