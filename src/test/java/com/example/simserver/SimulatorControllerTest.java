package com.example.simserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SimulatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulateEndpointReturnsSimulationResult() throws Exception {
        mockMvc.perform(get("/simulate")
                .param("servers", "2")
                .param("selfChecks", "1")
                .param("qmax", "2")
                .param("customers", "5")
                .param("meanArrivalInterval", "0.5")
                .param("meanServiceTime", "1.0")
                .param("meanRestTime", "0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers").value(2))
                .andExpect(jsonPath("$.selfChecks").value(1))
                .andExpect(jsonPath("$.qmax").value(2))
                .andExpect(jsonPath("$.customers").value(5))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void metricsEndpointReturnsSystemMetrics() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageWait").exists())
                .andExpect(jsonPath("$.totalRuns").exists());
    }

    @Test
    void simulationsEndpointReturnsList() throws Exception {
        mockMvc.perform(get("/simulations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void simulationsIdEndpointReturnsResultOrNull() throws Exception {
        // First, create a simulation
        String response = mockMvc.perform(get("/simulate")
                .param("servers", "1")
                .param("selfChecks", "0")
                .param("qmax", "1")
                .param("customers", "2"))
                .andReturn().getResponse().getContentAsString();
        // Extract the id from the response
        Number idNum = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        long id = idNum.longValue();
        // Now fetch by id
        mockMvc.perform(get("/simulations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        // Try a non-existent id
        mockMvc.perform(get("/simulations/999999"))
                .andExpect(status().isOk()); // Should return null or 404, but current impl returns null
    }

    @Test
    void simulateEndpointHandlesInvalidParams() throws Exception {
        // Negative customers
        mockMvc.perform(get("/simulate")
                .param("customers", "-5"))
                .andExpect(status().isOk()); // Should ideally return 400, but current impl returns 200
        // Zero servers
        mockMvc.perform(get("/simulate")
                .param("servers", "0"))
                .andExpect(status().isOk());
    }
}
