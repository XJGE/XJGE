package org.xjge.graphics;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * Created: May 11, 2021
 * <br><br>
 * Represents a texture atlas (or sprite sheet) that will provide the texture 
 * coordinates of sub-images within a larger {@link Texture}.
 * <p>
 * More specifically the data provided by this object includes:
 * <ul>
 * <li>The number of rows and columns the texture image was divided into.</li>
 * <li>The number of sub-images (or cells) the atlas contains.</li>
 * <li>The dimensions of a single sub-image cell as texture coordinates.</li>
 * <li>A {@linkplain subImageOffsets collection} containing the texture 
 *     coordinates of every sub-image cell.</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Atlas {

    public final int cellWidth;
    public final int cellHeight;
    public final int rows;
    public final int columns;
    public final int subImageCount;
    
    /**
     * The size (in texture coordinates) of a single sub-image cell. This 
     * information is useful in the event the atlas is used in a 
     * {@link SpriteAnimation} where consecutive frames are placed next to one 
     * another.
     */
    public final float subImageWidth, subImageHeight;
    
    public final Vector2f texCoords = new Vector2f();
    
    /**
     * A collection that couples the cell locations of sub-images to their 
     * corresponding coordinates within the texture.
     */
    public final Map<Vector2i, Vector2f> subImageOffsets;
    
    /**
     * Supplies the data generated from a texture and cell dimensions as a 
     * texture atlas. 
     * <p>
     * NOTE: The dimensions of the texture should be evenly divisible by the 
     * cell width and height.
     * 
     * @param texture the texture image to use
     * @param cellWidth the width of each sub-image cell in pixels
     * @param cellHeight the height of each sub-image cell in pixels
     */
    public Atlas(Texture texture, int cellWidth, int cellHeight) {
        this.cellWidth  = cellWidth;
        this.cellHeight = cellHeight;
        
        subImageWidth  = (float) cellWidth / texture.width;
        subImageHeight = (float) cellHeight / texture.height;
        rows           = texture.width / cellWidth;
        columns        = texture.height / cellHeight;
        subImageCount  = rows * columns;
        
        subImageOffsets = new HashMap<>() {{
            for(int x = 0; x < rows; x++) {
                for(int y = 0; y < columns; y++) {
                    put(new Vector2i(x, y), new Vector2f(subImageWidth * x, subImageHeight * y));
                }
            }
        }};
    }
    
}