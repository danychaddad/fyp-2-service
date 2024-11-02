package com.forestfire.app.nodes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NodeControllerIntegrationTest {
    @MockBean
    private NodeRepository nodeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("An API call to /nodes/all should return all nodes")
    void an_api_call_to_nodes_all_should_return_all_nodes() throws Exception {
        Node n = Node.builder().id("test-id").build();
        when(nodeRepository.findAll()).thenReturn(List.of(n));
        mockMvc.perform(get("/nodes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("test-id"));
    }
}