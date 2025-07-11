package org.xjge.core;

import java.text.DecimalFormat;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
class DGNoclip extends DebugGroup {

    private final DecimalFormat pattern = new DecimalFormat("0.0");
    
    public DGNoclip(String title) {
        super(title);
        for(int i = 0; i < 3; i++) output.add(new StringBuilder());
    }

    @Override
    protected void update(double deltaMetric, int fps, int entityCount, Noclip noclip) {
        for(int i = 0; i < output.size(); i++) {
            output.get(i).setLength(0);
            
            switch(i) {
                case 0 -> {
                    output.get(i).append("POSITION:  (").append(pattern.format(noclip.position.x)).append(", ")
                                                        .append(pattern.format(noclip.position.y)).append(", ")
                                                        .append(pattern.format(noclip.position.z)).append(")");
                }
                case 1 -> {
                    output.get(i).append("DIRECTION: (").append(pattern.format(noclip.direction.x)).append(", ")
                                                        .append(pattern.format(noclip.direction.y)).append(", ")
                                                        .append(pattern.format(noclip.direction.z)).append(")");
                }
                case 2 -> output.get(i).append("SPEED:     ").append("");
            }
        }
    }

    @Override
    protected void render() {
    }

}