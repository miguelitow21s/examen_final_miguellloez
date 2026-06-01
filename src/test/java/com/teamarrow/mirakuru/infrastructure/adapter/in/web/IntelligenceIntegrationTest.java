package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Verifies that intelligence is aggregated from multiple external sources for an
 * affected individual, ordered by confidence (highest first).
 */
@SpringBootTest
@AutoConfigureMockMvc
class IntelligenceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void gathersReportsFromMultipleSources() throws Exception {
        String id = registerAffected("Intel Subject");

        mockMvc.perform(get("/api/afectados/{id}/inteligencia", id))
                .andExpect(status().isOk())
                // both the A.R.G.U.S. and Street Informants adapters answer
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].sourceName", is("A.R.G.U.S.")))
                // sorted by confidence: satellite (70-99) before informants (30-59)
                .andExpect(jsonPath("$[0].confidence", greaterThanOrEqualTo(70)));
    }

    @Test
    void unknownAffectedReturns404() throws Exception {
        mockMvc.perform(get("/api/afectados/{id}/inteligencia",
                        "22222222-2222-2222-2222-222222222222"))
                .andExpect(status().isNotFound());
    }

    private String registerAffected(String codeName) throws Exception {
        String body = """
                {
                  "codeName": "%s",
                  "mirakuruSaturation": 80,
                  "aggressionIndex": 8,
                  "locationSector": "Docks",
                  "latitude": 0.0,
                  "longitude": 0.0
                }
                """.formatted(codeName);
        MvcResult result = mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asText();
    }
}
