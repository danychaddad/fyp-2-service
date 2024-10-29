package com.forestfire.app.sensors;

import com.forestfire.app.App;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    @DisplayName("all() should be invoked by a GET API call to the readings endpoint")
    void all_should_be_invoked_by_a_get_api_call_to_the_readings_endpoint() throws Exception {
        SensorReading reading = SensorReading.builder()
                .id("test-id")
                .gasSensorReading(1.5f)
                .humidity(1.2f)
                .temperature(3.2f).build();
        when(repo.findAll()).thenReturn(List.of(reading));
        mockMvc.perform(get("/readings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


    @Test
    @DisplayName("newReading() should correctly insert a new reading into the database")
    void new_reading_should_correctly_insert_a_new_reading_into_the_database() throws Exception {
        SensorReading reading = SensorReading.builder()
                .id("test-id")
                .gasSensorReading(1.5f)
                .humidity(1.2f)
                .temperature(3.2f)
                .build();

        // Mock the repository to return the reading when inserted
        when(repo.insert(any(SensorReading.class))).thenReturn(reading);

        Gson gson = new Gson();

        mockMvc.perform(post("/readings")
                        .contentType("application/json")
                        .content(gson.toJson(reading)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id")) // Check specific fields
                .andExpect(jsonPath("$.temperature").value(3.2))
                .andExpect(jsonPath("$.humidity").value(1.2))
                .andExpect(jsonPath("$.gasSensorReading").value(1.5));
    }
}