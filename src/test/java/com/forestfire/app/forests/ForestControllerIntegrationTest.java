package com.forestfire.app.forests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.google.gson.Gson;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class ForestControllerIntegrationTest {

    @MockBean
    private ForestRepository forestRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(forestRepository);
    }

    @Test
    @DisplayName("GET /forests should return all forests")
    void getAllForests_shouldReturnAllForests() throws Exception {
        Forest forest = Forest.builder().id("test-id").name("Amazon").build();
        when(forestRepository.findAll()).thenReturn(List.of(forest));

        mockMvc.perform(get("/forests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("test-id"))
                .andExpect(jsonPath("$[0].name").value("Amazon"));
    }

    @Test
    @DisplayName("GET /forests/{id} should return a forest by ID")
    void getForestById_shouldReturnForestById() throws Exception {
        String id = "test-id";
        Forest forest = Forest.builder().id(id).name("Amazon").build();
        when(forestRepository.findById(id)).thenReturn(Optional.of(forest));

        mockMvc.perform(get("/forests/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Amazon"));
    }

    @Test
    @DisplayName("POST /forests should add a new forest")
    void addForest_shouldAddNewForest() throws Exception {
        Forest forest = Forest.builder().id("test-id").name("Amazon").build();
        when(forestRepository.save(any(Forest.class))).thenReturn(forest);

        mockMvc.perform(post("/forests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(forest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.name").value("Amazon"));
    }

    @Test
    @DisplayName("PUT /forests/{id} should update an existing forest")
    void updateForest_shouldUpdateExistingForest() throws Exception {
        String id = "test-id";
        Forest updatedForest = Forest.builder().id(id).name("Updated Forest").build();
        when(forestRepository.save(any(Forest.class))).thenReturn(updatedForest);

        mockMvc.perform(put("/forests/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(updatedForest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated Forest"));
    }

    @Test
    @DisplayName("DELETE /forests/{id} should delete a forest by ID")
    void deleteForest_shouldDeleteForestById() throws Exception {
        String id = "test-id";
        doNothing().when(forestRepository).deleteById(id);

        mockMvc.perform(delete("/forests/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted forest with id: " + id));
    }
}
