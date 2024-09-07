package org.xjge.graphics;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

//Created: Jun 17, 2021

/**
 * Data structure that represents a single point of interest within a 
 * {@link Model} as defined by its file. Node objects are contained within a 
 * hierarchy and provide a unique name that can be used to traverse the tree. 
 * Nodes may also exhibit parent/child relationships with other nodes and are 
 * often used to calculate transformations in ascending order to move the 
 * {@link Bone bones} of an animated model.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
class Node {

    final String name;
    final Node parent;
    
    List<Node> children       = new ArrayList<>();
    List<Matrix4f> transforms = new ArrayList<>();
    
    /**
     * Constructs a new node object that will couple the name of a node 
     * (most often corresponding to a {@link Bone}) to a parent if one exists.
     * 
     * @param name the unique name used identify the node in the hierarchy
     * @param parent the parent node of this node
     */
    Node(String name, Node parent) {
        this.name   = name;
        this.parent = parent;
    }
    
    /**
     * Calculates the transformation of a node relative to its parents.
     * 
     * @param node the top level node to start calculating from
     * @param frameNum the index of the animations current {@link KeyFrame}.
     * 
     * @return the final calculated transformation matrix
     */
    static Matrix4f getParentTransform(Node node, int frameNum) {
        if(node == null) {
            return new Matrix4f();
        } else {
            Matrix4f parentTransform  = new Matrix4f(getParentTransform(node.parent, frameNum));
            List<Matrix4f> transforms = node.transforms;
            
            Matrix4f nodeTransform;
            
            if(frameNum < transforms.size()) nodeTransform = transforms.get(frameNum);
            else if(!transforms.isEmpty())   nodeTransform = transforms.get(transforms.size() - 1);
            else                             nodeTransform = new Matrix4f();
            
            return parentTransform.mul(nodeTransform);
        }
    }
    
    /**
     * Locates a node within the hierarchy by using its unique name.
     * 
     * @param nodeName the name of the node to return
     * 
     * @return the node of this name or null if no such node exists
     */
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
    
    /**
     * Finds the total number of {@link KeyFrame KeyFrames} (represented as 
     * transformations) this node has in relation to the current animation.
     * 
     * @return the number of transforms this node has in the current animation.
     */
    int getNumKeyFrames() {
        int numFrames = transforms.size();
        
        for(Node node : children) {
            int childFrame = node.getNumKeyFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        
        return numFrames;
    }
    
}