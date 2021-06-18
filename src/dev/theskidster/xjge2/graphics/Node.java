package dev.theskidster.xjge2.graphics;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class Node {

    final String name;
    final Node parent;
    
    List<Node> children       = new ArrayList<>();
    List<Matrix4f> transforms = new ArrayList<>();
    
    Node(String name, Node parent) {
        this.name   = name;
        this.parent = parent;
    }
    
    static Matrix4f getParentTransform(Node node, int frameNum) {
        if(node == null) {
            return new Matrix4f();
        } else {
            Matrix4f parentTransform  = new Matrix4f(getParentTransform(node.parent, frameNum));
            List<Matrix4f> transforms = node.transforms;
            
            Matrix4f nodeTransform;
            
            if(frameNum < transforms.size()) nodeTransform = transforms.get(frameNum);
            else if(transforms.size() > 0)   nodeTransform = transforms.get(transforms.size() - 1);
            else                             nodeTransform = new Matrix4f();
            
            return parentTransform.mul(nodeTransform);
        }
    }
    
    Node getNodeByName(String nodeName) {
        Node result = null;
        
        if(name.equals(nodeName)) {
            result = this;
        } else {
            for(Node node : children) {
                result = node.getNodeByName(nodeName);
                if(result != null) break;
            }
        }
        
        return result;
    }
    
    int getNumKeyFrames() {
        int numFrames = transforms.size();
        
        for(Node node : children) {
            int childFrame = node.getNumKeyFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        
        return numFrames;
    }
    
}