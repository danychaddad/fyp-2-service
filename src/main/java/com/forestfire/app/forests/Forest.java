package com.forestfire.app.forests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.forestfire.app.forests.vertex.Vertex;
import com.forestfire.app.nodes.Node;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("forests")
public class Forest {
    @Id
    private String id;    
    
    private String name;

    @Builder.Default
    private List<Node> nodesInForest = new ArrayList<>();

    @Builder.Default
    private List<Vertex> verticesOfForest = new ArrayList<>();

    // @Builder.Default
    // public List<CameraNode> camerasInForest new ArrayList<>();

    public boolean addVertex(Vertex vertex) {
        return verticesOfForest.add(vertex);
    }   
    
    public boolean updateVertex(Vertex vertex) {
        for (int i = 0; i < verticesOfForest.size(); i++) {
            if (verticesOfForest.get(i).getId().equals(vertex.getId())) {
                verticesOfForest.set(i, vertex);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeVertex(Vertex vertex) {
        return verticesOfForest.removeIf(v -> v.getId().equals(vertex.getId()));
    }

    public boolean addNode(Node node) {
        return nodesInForest.add(node);
    }

    public boolean updateNode(Node node) {
        for (int i = 0; i < nodesInForest.size(); i++) {
            if (nodesInForest.get(i).getId().equals(node.getId())) {
                nodesInForest.set(i, node);
                return true;
            }
        }
        return false;
    }

    public boolean removeNode(Node node) {
        return nodesInForest.removeIf(n -> n.getId().equals(node.getId()));
    }

    // public boolean addCameraNode(CameraNode cameraNode) {
    //     return camerasInForest.add(cameraNode);
    // }

    // public boolean updateCameraNode(CameraNode cameraNode) {
    //     for (int i = 0; i < camerasInForest.size(); i++) {
    //         if (camerasInForest.get(i).getId().equals(cameraNode.getId())) {
    //             camerasInForest.set(i, cameraNode);
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    // public boolean removeCameraNode(CameraNode cameraNode) {
    //     return camerasInForest.removeIf(c -> c.getId().equals(cameraNode.getId()));
    // }
}
