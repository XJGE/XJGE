package org.xjge.core;

import org.xjge.ui.ScrollBar;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class EMGLoadedAssets extends EngineMetricsGroup {

    private final ScrollBar scrollBar;
    
    public EMGLoadedAssets(String title, int contentAreaHeight) {
        super(title, contentAreaHeight);
        scrollBar = new ScrollBar(contentArea);
    }

    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        scrollBar.relocate();
    }

    @Override
    protected void render() {
        scrollBar.render();
    }

}