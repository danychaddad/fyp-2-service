package com.forestfire.app.sensors;

import com.forestfire.app.App;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
class SensorReadingControllerIntegrationTest {

    @MockBean
    private SensorReadingRepository repo;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    List<SensorReading> sensorReadingStore;
    Gson gson = new Gson();

    @BeforeEach
    void setup() {
        sensorReadingStore = new ArrayList<>();
        when(repo.findAll()).thenReturn(sensorReadingStore);
        when(repo.insert(any(SensorReading.class))).thenAnswer(invocationOnMock -> {
            SensorReading reading = invocationOnMock.getArgument(0);
            reading.setId("test-id");
            sensorReadingStore.add(reading);
            return reading;
        });
        when(repo.insert(any(List.class))).thenAnswer(invocationOnMock -> {
            List<SensorReading> readings = invocationOnMock.getArgument(0);
            List<SensorReading> inserted = new ArrayList<SensorReading>();
            for (SensorReading reading : readings) {
                inserted.add(repo.insert(reading));
            }
            return inserted;
        });
        when(repo.findSensorReadingsByNodeId(any(String.class))).thenAnswer(invocationOnMock -> {
            String nodeId = invocationOnMock.getArgument(0);
            return sensorReadingStore.stream()
                    .filter(sensorReading -> sensorReading.getNodeId().equals(nodeId)).toList();
        });
    }


    @Test
    @DisplayName("all() should be invoked by a GET API call to the readings endpoint")
    void all_should_be_invoked_by_a_get_api_call_to_the_readings_endpoint() throws Exception {
        SensorReading reading = SensorReading.builder()
                .gasSensorReading(1.5f)
                .humidity(1.2f)
                .temperature(3.2f).build();
        repo.insert(reading);
        mockMvc.perform(get("/readings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


    @Test
    @DisplayName("newReading() should correctly insert a new reading into the database")
    void new_reading_should_correctly_insert_a_new_reading_into_the_database() throws Exception {
        SensorReading reading = SensorReading.builder()
                .gasSensorReading(1.5f)
                .humidity(1.2f)
                .temperature(3.2f)
                .nodeId("node-id")
                .build();

        mockMvc.perform(post("/readings")
                        .contentType("application/json")
                        .content(gson.toJson(reading)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.temperature").value(3.2))
                .andExpect(jsonPath("$.humidity").value(1.2))
                .andExpect(jsonPath("$.nodeId").value("node-id"))
                .andExpect(jsonPath("$.gasSensorReading").value(1.5));
    }

    @Test
    @DisplayName("A GET request to /readings/{nodeID} should return all readings for a node")
    void a_get_request_to_readings_node_id_should_return_all_readings_for_a_node() throws Exception {
        SensorReading reading1 = getRandomReadingWithNodeId("node1");
        SensorReading reading2 = getRandomReadingWithNodeId("node2");
        SensorReading reading3 = getRandomReadingWithNodeId("node3");
        repo.insert(List.of(reading1, reading2, reading3));
        mockMvc.perform(get("/readings/node1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nodeId").value("node1"));
    }

    // @Test
    // @DisplayName("A POST request to /nodes/{nodeId}/readings should create a new sensor reading")
    // void a_post_request_to_nodes_nodeId_readings_should_create_a_new_sensor_reading() throws Exception {
    //     String nodeId = "node-1";
    //     SensorReading newReading = SensorReading.builder()
    //             .nodeId(nodeId)
    //             .temperature(25.5f)
    //             .humidity(60.0f)
    //             .gasSensorReading(0.3f)
    //             .timestamp(new Date())
    //             .build();

    //     mockMvc.perform(post("/nodes/" + nodeId + "/readings")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(newReading)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.nodeId").value(nodeId))
    //             .andExpect(jsonPath("$.temperature").value(25.5))
    //             .andExpect(jsonPath("$.humidity").value(60.0))
    //             .andExpect(jsonPath("$.gasSensorReading").value(0.3))
    //             .andExpect(jsonPath("$.timestamp").exists());
    // }

    // @Test
    // @DisplayName("A GET request to /nodes/{nodeId}/readings should return the correct sensor readings")
    // void a_get_request_to_nodes_nodeId_readings_should_return_the_correct_sensor_readings() throws Exception {
    //     String nodeId = "node-1";
    //     SensorReading reading1 = SensorReading.builder()
    //             .nodeId(nodeId)
    //             .temperature(25.5f)
    //             .humidity(60.0f)
    //             .gasSensorReading(0.3f)
    //             .timestamp(new Date())
    //             .build();
    //     SensorReading reading2 = SensorReading.builder()
    //             .nodeId(nodeId)
    //             .temperature(26.0f)
    //             .humidity(61.0f)
    //             .gasSensorReading(0.4f)
    //             .timestamp(new Date())
    //             .build();
    //     repo.insert(reading1);
    //     repo.insert(reading2);

    //     mockMvc.perform(get("/nodes/" + nodeId + "/readings"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].nodeId").value(nodeId))
    //             .andExpect(jsonPath("$[0].temperature").value(25.5))
    //             .andExpect(jsonPath("$[0].humidity").value(60.0))
    //             .andExpect(jsonPath("$[0].gasSensorReading").value(0.3))
    //             .andExpect(jsonPath("$[0].timestamp").exists())
    //             .andExpect(jsonPath("$[1].nodeId").value(nodeId))
    //             .andExpect(jsonPath("$[1].temperature").value(26.0))
    //             .andExpect(jsonPath("$[1].humidity").value(61.0))
    //             .andExpect(jsonPath("$[1].gasSensorReading").value(0.4))
    //             .andExpect(jsonPath("$[1].timestamp").exists());
    // }

    SensorReading getRandomReadingWithNodeId(String nodeId) {
        float humidity = (float) (Math.random() * 60);
        float temp = (float) (Math.random() * 60);
        float gas = (float) (Math.random() * 60);
        return SensorReading.builder()
                .nodeId(nodeId)
                .gasSensorReading(gas)
                .temperature(temp)
                .humidity(humidity)
                .build();
    }
}