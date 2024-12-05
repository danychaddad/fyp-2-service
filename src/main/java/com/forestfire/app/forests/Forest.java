package com.forestfire.app.forests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.forestfire.app.forests.vertex.Vertex;

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
    private List<Vertex> verticesOfForest = new ArrayList<>();

    private ForestFormulaParams formulaParams;

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

    public float getNodeDistance() {
        return -2.078f
                + 0.045f * formulaParams.getX1()
                + 0.181f * formulaParams.getX2()
                + 0.347f * formulaParams.getX3()
                + 0.04f * formulaParams.getX4()
                + 0.014f * formulaParams.getX5()
                + 0.094f * formulaParams.getX6()
                + 0.03f * formulaParams.getX7()
                + 0.042f * formulaParams.getX8()
                + 0.202f * formulaParams.getX9()
                + 0.004f * formulaParams.getX10();
    }
}
