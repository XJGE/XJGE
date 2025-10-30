package org.xjge.core;

import java.util.List;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class TCReloadAsset extends TerminalCommand {

    TCReloadAsset() {
        super("", //TODO: add description for this command
              "", 
              "");
    }
    
    @Override
    public TerminalOutput execute(List<String> args) {
        if(!args.isEmpty()) {
            String filename = args.get(0);
            
            if(AssetManager.reload(filename)) {
                return new TerminalOutput("Asset file: \"" + filename + "\" reloaded successfully", Color.WHITE);
            } else {
                if(AssetManager.exists(filename)) {
                    return new TerminalOutput("ERROR: Failed to reload asset file \"" + filename + "\"", Color.RED);
                } else {
                    return new TerminalOutput("ERROR: Could not locate a file by the name: \"" + filename + "\"", Color.RED);
                }
            }
        } else {
            return new TerminalOutput(errorNotEnoughArgs(1), Color.RED);
        }
    }

}