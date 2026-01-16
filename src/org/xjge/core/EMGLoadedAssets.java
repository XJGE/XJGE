package org.xjge.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.xjge.ui.ScrollBar;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EMGLoadedAssets extends EngineMetricsGroup {

    private static boolean updateOutput;
    private final ScrollBar scrollBar;
    private static final List<Asset> loadedAssets = new LinkedList<>();
    
    public EMGLoadedAssets(String title, int contentAreaHeight) {
        super(title, contentAreaHeight);
        scrollBar = new ScrollBar(contentArea);
    }

    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        scrollBar.relocate();
        
        if(updateOutput) {
            for(Asset loadedAsset : loadedAssets) {
                output.add(new StringBuilder(loadedAsset.getFilename()));
            }
            
            updateOutput = false;
        }
    }

    @Override
    protected void render() {
        scrollBar.render();
    }
    
    static void rebuildList(Map<String, Asset> assets) {
        loadedAssets.clear();
        loadedAssets.addAll(assets.values());
        updateOutput = true;
    }

}