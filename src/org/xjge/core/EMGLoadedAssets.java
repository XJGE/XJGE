package org.xjge.core;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
    private static final Set<String> loadedFiles = new TreeSet<>();
    
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
            
            outputLength += 10; //Applies padding to bottom of list so text isn't partially cut off during the scissor test
            
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
        
        for(var asset : assets.values()) {
            if(AssetManager.isEngineAsset(asset.getFilename())) continue;
            loadedFiles.add(asset.getFilename());
        }
        
        updateOutput = true;
    }

}