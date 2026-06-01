package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

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
 * End-to-end test of the mission coordination flow over the real wiring:
 * register a target -> plan a mission (required operatives derived from the
 * target's threat level) -> assign operatives -> launch, including the
 * understaffed (400) and unknown-operative (404) failure paths.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MissionFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void planAssignAndLaunchAgainstModerateTarget() throws Exception {
        // saturation 50 + aggression 5 -> MODERATE -> requires 2 operatives
        String targetId = registerAffected("Target Alpha", 50, 5);

        String missionId = planMission("Operation Alpha", targetId, 2);

        // one operative is not enough
        assignOperative(missionId, "Green Arrow")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedOperatives", is(1)))
                .andExpect(jsonPath("$.readyToLaunch", is(false)));

        mockMvc.perform(post("/api/misiones/{id}/lanzar", missionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Mission needs 2 operatives but only 1 are assigned")));

        // second operative makes it ready
        assignOperative(missionId, "Spartan")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedOperatives", is(2)))
                .andExpect(jsonPath("$.readyToLaunch", is(true)));

        mockMvc.perform(post("/api/misiones/{id}/lanzar", missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void assigningUnknownOperativeReturns404() throws Exception {
        String targetId = registerAffected("Target Bravo", 50, 5);
        String missionId = planMission("Operation Bravo", targetId, 2);

        mockMvc.perform(post("/api/misiones/{id}/operativos", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operativeCodeName\":\"Nonexistent Hero\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void rosterIsAvailable() throws Exception {
        mockMvc.perform(get("/api/equipo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(6)));
    }

    // ----- helpers -----

    private String registerAffected(String codeName, int saturation, int aggression) throws Exception {
        String body = """
                {
                  "codeName": "%s",
                  "mirakuruSaturation": %d,
                  "aggressionIndex": %d,
                  "locationSector": "Glades",
                  "latitude": 0.0,
                  "longitude": 0.0
                }
                """.formatted(codeName, saturation, aggression);
        MvcResult result = mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private String planMission(String name, String targetId, int expectedRequired) throws Exception {
        String body = """
                { "name": "%s", "targetAffectedId": "%s" }
                """.formatted(name, targetId);
        MvcResult result = mockMvc.perform(post("/api/misiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requiredOperatives", is(expectedRequired)))
                .andExpect(jsonPath("$.status", is("PLANNING")))
                .andReturn();
        return idFrom(result);
    }

    private org.springframework.test.web.servlet.ResultActions assignOperative(String missionId,
                                                                               String operativeCodeName)
            throws Exception {
        String body = "{\"operativeCodeName\":\"" + operativeCodeName + "\"}";
        return mockMvc.perform(post("/api/misiones/{id}/operativos", missionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String idFrom(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asText();
    }
}
