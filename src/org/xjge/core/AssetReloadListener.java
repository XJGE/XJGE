package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
@FunctionalInterface
public interface AssetReloadListener {
    
    void onAssetReload(Asset asset);
    
}