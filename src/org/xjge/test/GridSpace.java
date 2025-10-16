package org.xjge.test;

import static org.xjge.test.GridSpaceStatus.NONE;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class GridSpace {
    
    public boolean muddy; //TODO: Simple boolean for now but we might end up having an enum control this
    public boolean target; //TODO: band-aid fix only used for mud minigame targeting
    
    public final boolean[] unreachableEdge = new boolean[4];
    
    public static final int TYPE_OPEN         = 0;
    public static final int TYPE_SOLID        = 1;
    public static final int TYPE_SPAWN_PLAYER = 2;
    public static final int TYPE_SPAWN_ENEMY  = 3;
    public static final int TYPE_SPAWN_ITEM   = 4;
    
    public final int type;
    public final int xLocation;
    public final int yLocation;
    public final int zLocation;
    
    public static final float SIZE = 1f;
    
    public ComponentUnit occupyingUnit;
    public GridSpace previousSpace;
    
    public GridSpaceStatus status = NONE;
    
    GridSpace(int type, int xLocation, int yLocation, int zLocation) {
        this.type      = type;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.zLocation = zLocation;
    }
    
}