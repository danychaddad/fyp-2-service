package com.forestfire.app.sensors;

import com.forestfire.app.App;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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


    // @Test
    // @DisplayName("newReading() should correctly insert a new reading into the database")
    // void new_reading_should_correctly_insert_a_new_reading_into_the_database() throws Exception {
    //     SensorReading reading = SensorReading.builder()
    //             .gasSensorReading(1.5f)
    //             .humidity(1.2f)
    //             .temperature(3.2f)
    //             .nodeId("node-id")
    //             .build();

    //     mockMvc.perform(post("/readings")
    //                     .contentType("application/json")
    //                     .content(gson.toJson(reading)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.id").value("test-id"))
    //             .andExpect(jsonPath("$.temperature").value(3.2))
    //             .andExpect(jsonPath("$.humidity").value(1.2))
    //             .andExpect(jsonPath("$.nodeId").value("node-id"))
    //             .andExpect(jsonPath("$.gasSensorReading").value(1.5));
    // }

    // @Test
    // @DisplayName("A GET request to /readings/{nodeID} should return all readings for a node")
    // void a_get_request_to_readings_node_id_should_return_all_readings_for_a_node() throws Exception {
    //     SensorReading reading1 = getRandomReadingWithNodeId("node1");
    //     SensorReading reading2 = getRandomReadingWithNodeId("node2");
    //     SensorReading reading3 = getRandomReadingWithNodeId("node3");
    //     repo.insert(List.of(reading1, reading2, reading3));
    //     mockMvc.perform(get("/readings/node1"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$", hasSize(1)))
    //             .andExpect(jsonPath("$[0].nodeId").value("node1"));
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