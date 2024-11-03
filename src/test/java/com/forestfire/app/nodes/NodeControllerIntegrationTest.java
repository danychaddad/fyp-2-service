package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NodeControllerIntegrationTest {
    @MockBean
    private NodeRepository nodeRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(nodeRepository);
    }

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

    @Test
    @DisplayName("A POST request to /nodes/hello should initialize a node")
    void a_post_request_to_nodes_hello_should_initialize_a_node() throws Exception {
        Gson gson = new Gson();
        String macAddress = "12:34:56:78:9A:BC";
        String requestBody = gson.toJson(NodeHelloRequest.builder().macAddress(macAddress).build());
        System.out.println(requestBody);
        mockMvc.perform(post("/nodes/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("A GET request after a POST request should correctly return the new node")
    void a_get_request_after_a_post_request_should_correctly_return_the_new_node() throws Exception {
        List<Node> nodeStore = new ArrayList<>();
        when(nodeRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(nodeStore));

        Gson gson = new Gson();
        String macAddress = "12:34:56:78:9A:BC";

        mockMvc.perform(get("/nodes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        Node n = Node.builder().macAddress(macAddress).build();
        when(nodeRepository.save(any(Node.class))).thenAnswer(invocation -> {
            nodeStore.add(n);
            return n;
        });

        mockMvc.perform(post("/nodes/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(NodeHelloRequest.builder().macAddress(macAddress).build())))
                .andExpect(status().isCreated());

        when(nodeRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(nodeStore));


        mockMvc.perform(get("/nodes/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].macAddress").value(macAddress));
    }

}