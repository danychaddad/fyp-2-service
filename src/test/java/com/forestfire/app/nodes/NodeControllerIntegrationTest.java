package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;
import com.forestfire.app.sensors.SensorReadingRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NodeControllerIntegrationTest {
        @MockBean
        private NodeRepository nodeRepository;

        @MockBean
        private SensorReadingRepository sensorReadingRepository;

        @Autowired
        private MockMvc mockMvc;

        private final Gson gson = new Gson();

        List<Node> nodeStore;

        @BeforeEach
        void setUp() {
                nodeStore = new ArrayList<>();
                reset(nodeRepository);
                when(nodeRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(nodeStore));
                when(nodeRepository.save(any(Node.class))).thenAnswer(invocation -> {
                        Node node = invocation.getArgument(0);
                        nodeStore.add(node);
                        return node;
                });
                when(nodeRepository.findById(any(String.class))).thenAnswer(invocation -> {
                        String wantedMac = invocation.getArgument(0);
                        return nodeStore.stream()
                                        .filter(node -> node.getMacAddress().equals(wantedMac))
                                        .findFirst();
                });
        }

        // @Test
        // @DisplayName("An API call to /nodes should return all nodes")
        // void an_api_call_to_nodes_all_should_return_all_nodes() throws Exception {
        //         Node n = Node.builder().macAddress("test-id").build();
        //         nodeRepository.save(n);
        //         mockMvc.perform(get("/nodes"))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$", hasSize(1)))
        //                         .andExpect(jsonPath("$[0].macAddress").value("test-id"));
        // }

        // @Test
        // @DisplayName("A POST request to /nodes/hello should initialize a node with the correct parameters")
        // void a_post_request_to_nodes_hello_should_initialize_a_node() throws Exception {
        //         Gson gson = new Gson();
        //         String macAddress = "12:34:56:78:9A:BC";
        //         String requestBody = gson.toJson(NodeHelloRequest.builder()
        //                         .macAddress(macAddress)
        //                         .latitude(1f)
        //                         .longitude(2f)
        //                         .build());
        //         System.out.println(requestBody);
        //         mockMvc.perform(post("/nodes/hello")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(requestBody))
        //                         .andExpect(jsonPath("$.latitude").value(1.0))
        //                         .andExpect(jsonPath("$.longitude").value(2.0))
        //                         .andExpect(status().isCreated());
        // }

        // @Test
        // @DisplayName("A GET request after a POST request should correctly return the new node")
        // void a_get_request_after_a_post_request_should_correctly_return_the_new_node() throws Exception {
        //         String macAddress = "12:34:56:78:9A:BC";
        //         float longitude = 1.5f;

        //         mockMvc.perform(get("/nodes"))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$", hasSize(0)));

        //         mockMvc.perform(post("/nodes/hello")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(gson.toJson(NodeHelloRequest.builder().macAddress(macAddress)
        //                                         .longitude(longitude).build())))
        //                         .andExpect(status().isCreated());

        //         mockMvc.perform(get("/nodes"))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$", hasSize(1)))
        //                         .andExpect(jsonPath("$[0].macAddress").value(macAddress))
        //                         .andExpect(jsonPath("$[0].longitude").value(longitude));
        // }

        // @Test
        // @DisplayName("A POST request to /nodes/hello should return the existing node if it already exists in the DB")
        // void a_post_request_to_nodes_hello_should_return_the_existing_node_if_it_already_exists_in_the_db()
        //                 throws Exception {
        //         String testedMacAddress = "test-mac";
        //         float testedLongitude = 1f;
        //         float expectedLatitude = 2f;
        //         NodeHelloRequest req1 = NodeHelloRequest.builder()
        //                         .macAddress(testedMacAddress)
        //                         .longitude(testedLongitude)
        //                         .latitude(expectedLatitude).build();
        //         NodeHelloRequest req2 = NodeHelloRequest.builder()
        //                         .macAddress(testedMacAddress)
        //                         .longitude(testedLongitude)
        //                         .latitude(3).build();

        //         mockMvc.perform(post("/nodes/hello").contentType(MediaType.APPLICATION_JSON)
        //                         .content(gson.toJson(req1)))
        //                         .andExpect(status().isCreated())
        //                         .andExpect(jsonPath("$.macAddress").value(testedMacAddress))
        //                         .andExpect(jsonPath("$.longitude").value(testedLongitude))
        //                         .andExpect(jsonPath("$.latitude").value(expectedLatitude));

        //         mockMvc.perform(post("/nodes/hello").contentType(MediaType.APPLICATION_JSON)
        //                         .content(gson.toJson(req2)))
        //                         .andExpect(status().isAccepted())
        //                         .andExpect(jsonPath("$.macAddress").value(testedMacAddress))
        //                         .andExpect(jsonPath("$.longitude").value(testedLongitude))
        //                         .andExpect(jsonPath("$.latitude").value(expectedLatitude));
        // }

        // @Test
        // @DisplayName("A GET request to /nodes/{nodeId} should return the correct node")
        // void a_get_request_to_nodes_node_id_should_return_the_correct_node() throws Exception {
        //         Node expected = Node.builder()
        //                         .macAddress("test-id")
        //                         .forestId("forest-1")
        //                         .longitude(1f)
        //                         .latitude(2f)
        //                         .build();
        //         nodeRepository.save(expected);
        //         mockMvc.perform(get("/nodes/test-id"))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$.macAddress").value(expected.getMacAddress()))
        //                         .andExpect(jsonPath("$.forestId").value(expected.getForestId()))
        //                         .andExpect(jsonPath("$.latitude").value(expected.getLatitude()))
        //                         .andExpect(jsonPath("$.longitude").value(expected.getLongitude()));
        // }

        // @Test
        // @DisplayName("A GET request to /nodes/{nodeId} should return 404 if not found")
        // void a_get_request_to_nodes_node_id_should_return_404_if_not_found() throws Exception {
        //         mockMvc.perform(get("/nodes/test-id"))
        //                         .andExpect(status().isNotFound());
        // }

        // @Test
        // @DisplayName("A GET request to /forests/{forestId}/nodes should return the correct nodes")
        // void a_get_request_to_forest_forestId_nodes_should_return_the_correct_nodes() throws Exception {
        //         String forestId = "forest-1";
        //         Node node1 = Node.builder()
        //                         .macAddress("node-1")
        //                         .forestId(forestId)
        //                         .longitude(1f)
        //                         .latitude(2f)
        //                         .build();
        //         Node node2 = Node.builder()
        //                         .macAddress("node-2")
        //                         .forestId(forestId)
        //                         .longitude(3f)
        //                         .latitude(4f)
        //                         .build();
        //         nodeRepository.save(node1);
        //         nodeRepository.save(node2);

        //         List<Node> nodes = List.of(node1, node2);
        //         Mockito.when(nodeRepository.findByForestId(forestId)).thenReturn(nodes);

        //         mockMvc.perform(get("/forests/" + forestId + "/nodes"))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$[0].macAddress").value(node1.getMacAddress()))
        //                         .andExpect(jsonPath("$[0].forestId").value(node1.getForestId()))
        //                         .andExpect(jsonPath("$[0].latitude").value(node1.getLatitude()))
        //                         .andExpect(jsonPath("$[0].longitude").value(node1.getLongitude()))
        //                         .andExpect(jsonPath("$[1].macAddress").value(node2.getMacAddress()))
        //                         .andExpect(jsonPath("$[1].forestId").value(node2.getForestId()))
        //                         .andExpect(jsonPath("$[1].latitude").value(node2.getLatitude()))
        //                         .andExpect(jsonPath("$[1].longitude").value(node2.getLongitude()));
        // }

        // @Test
        // @DisplayName("A GET request to /forest/{forestId}/nodes should return 404 if no nodes are found")
        // void a_get_request_to_forest_forestId_nodes_should_return_404_if_no_nodes_are_found() throws Exception {
        //         String forestId = "non-existent-forest";
        //         mockMvc.perform(get("/forests/" + forestId + "/nodes"))
        //                         .andExpect(status().isNotFound());
        // }

        // @Test
        // @DisplayName("DELETE /nodes/{id} should delete a node by ID")
        // void deleteNode_shouldDeleteNodeById() throws Exception {
        //         String id = "test-id";
        //         doNothing().when(nodeRepository).deleteById(id);

        //         mockMvc.perform(delete("/nodes/{id}", id))
        //                         .andExpect(status().isOk())
        //                         .andExpect(content().string("Deleted node with id: " + id));
        // }
}