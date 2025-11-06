package org.xjge.core;

import java.text.DecimalFormat;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class EMGPerformance extends EngineMetricsGroup {
    
    private long memoryFree;
    private long memoryUsed;
    
    private final DecimalFormat pattern = new DecimalFormat("0");
    
    EMGPerformance(String title) {
        super(title);
        for(int i = 0; i < 4; i++) output.add(new StringBuilder());
    }
    
    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        memoryFree = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        
        for(int i = 0; i < output.size(); i++) {
            output.get(i).setLength(0);
            
            switch(i) {
                case 0 -> output.get(i).append("FPS:          ").append(fps);
                case 1 -> output.get(i).append("DELTA TIME:   ").append(pattern.format(deltaMetric * 1000)).append(" ms");
                case 2 -> output.get(i).append("MEMORY:       ").append(memoryUsed).append(" MB (used) / ").append(memoryFree).append(" MB (free)");
                case 3 -> output.get(i).append("ENTITY COUNT: ").append(entityCount);
            }
        }
    }

    @Override
    protected void render() {}

}