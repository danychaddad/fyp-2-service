package com.forestfire.app.forests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.forestfire.app.forests.vertex.Vertex;
import com.forestfire.app.nodes.Node;

public class ForestTest {
    Forest f;

    @BeforeEach
    void setup() {
        f = Forest.builder()
                .id("test-id")
                .name("test-name")
                .build();
}

    @Test
    @DisplayName("addVertex() should correctly add a vertex to the list of vertices")
    void add_vertex_should_correctly_add_a_vertex_to_the_list_of_vertices() {
        Vertex v = Vertex.builder().build();
        assertTrue(f.addVertex(v));
        assertEquals(f.getVerticesOfForest().size(), 1);
    }

    @Test
    @DisplayName("updateVertex() should correctly update a vertex in the list of vertices")
    void update_vertex_should_correctly_update_a_vertex_in_the_list_of_vertices() {
        Vertex v = Vertex.builder().id("test-id").build();
        f.addVertex(v);
        Vertex updatedVertex = Vertex.builder().id("test-id").build();
        assertTrue(f.updateVertex(updatedVertex));
        assertEquals(f.getVerticesOfForest().get(0).getId(), "test-id");
    }

    @Test
    @DisplayName("removeVertex() should correctly remove a vertex from the list of vertices")
    void remove_vertex_should_correctly_remove_a_vertex_from_the_list_of_vertices() {
        Vertex v = Vertex.builder().id("test-id").build();
        f.addVertex(v);
        assertTrue(f.removeVertex(v));
        assertEquals(f.getVerticesOfForest().size(), 0);
    }

    @Test
    @DisplayName("addNode() should correctly add a node to the list of nodes")
    void add_node_should_correctly_add_a_node_to_the_list_of_nodes() {
        Node n = Node.builder().build();
        assertTrue(f.addNode(n));
        assertEquals(f.getNodesInForest().size(), 1);
    }

    @Test
    @DisplayName("updateNode() should correctly update a node in the list of nodes")
    void update_node_should_correctly_update_a_node_in_the_list_of_nodes() {
        Node n = Node.builder().id("test-id").build();
        f.addNode(n);
        Node updatedNode = Node.builder().id("test-id").build();
        assertTrue(f.updateNode(updatedNode));
        assertEquals(f.getNodesInForest().get(0).getId(), "test-id");
    }

    @Test
    @DisplayName("removeNode() should correctly remove a node from the list of nodes")
    void remove_node_should_correctly_remove_a_node_from_the_list_of_nodes() {
        Node n = Node.builder().id("test-id").build();
        f.addNode(n);
        assertTrue(f.removeNode(n));
        assertEquals(f.getNodesInForest().size(), 0);
    }
}
