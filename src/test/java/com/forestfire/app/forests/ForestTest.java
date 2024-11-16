package com.forestfire.app.forests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.forestfire.app.forests.vertex.Vertex;

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
}
