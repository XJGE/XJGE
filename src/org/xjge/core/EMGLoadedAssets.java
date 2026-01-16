package org.xjge.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.xjge.ui.Font;
import org.xjge.ui.ScrollBar;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EMGLoadedAssets extends EngineMetricsGroup {

    private static boolean updateOutput;
    private final ScrollBar scrollBar;
    private static final List<String> loadedFiles = new LinkedList<>();
    
    public EMGLoadedAssets(String title, int contentAreaHeight) {
        super(title, contentAreaHeight);
        scrollBar = new ScrollBar(contentArea, true);
    }

    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        scrollBar.relocate();
        
        if(updateOutput && expanded) {
            output.clear();
            int outputLength = 0;
            
            for(String filename : loadedFiles) {
                output.add(new StringBuilder(filename));
                outputLength += Font.FALLBACK.getSize();
            }
            
            scrollBar.setContentAreaLength(outputLength);
            updateOutput = false;
        }
        
        contentOffset = (scrollBar.getContentOffset());
    }

    @Override
    protected void render() {
        scrollBar.render();
    }

    @Override
    protected void processMouseInput(Mouse mouse) {
        scrollBar.processMouseInput(mouse);
    }
    
    static void rebuildList(Map<String, Asset> assets) {
        loadedFiles.clear();
        for(var asset : assets.values()) loadedFiles.add(asset.getFilename());
        updateOutput = true;
    }

}