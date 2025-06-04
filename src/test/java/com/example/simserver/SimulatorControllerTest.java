package com.example.simserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimulatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulateEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/simulate"))
            .andExpect(status().isOk());
    }
}
