package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
 * End-to-end test of the four REST endpoints over the full (real) wiring:
 * controller -> use case -> domain -> in-memory repository. No mocks are used,
 * which exercises the architecture exactly as it runs in production.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AffectedControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REGISTER_BODY = """
            {
              "codeName": "Deathstroke",
              "mirakuruSaturation": 95,
              "aggressionIndex": 9,
              "locationSector": "Glades",
              "latitude": 40.71,
              "longitude": -74.0
            }
            """;

    @Test
    void registersAndClassifiesAffectedAsCritical() throws Exception {
        mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codeName", is("Deathstroke")))
                .andExpect(jsonPath("$.threatLevel", is("CRITICAL")))
                .andExpect(jsonPath("$.status", is("AT_LARGE")));
    }

    @Test
    void registersThenGetsById() throws Exception {
        String id = registerAndReturnId("""
                {
                  "codeName": "Mirakuru Soldier 7",
                  "mirakuruSaturation": 60,
                  "aggressionIndex": 6,
                  "locationSector": "Docks",
                  "latitude": 0.0,
                  "longitude": 0.0
                }
                """);

        mockMvc.perform(get("/api/afectados/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.codeName", is("Mirakuru Soldier 7")));
    }

    @Test
    void updatesAffectedToNeutralized() throws Exception {
        String id = registerAndReturnId("""
                {
                  "codeName": "Roy Harper",
                  "mirakuruSaturation": 70,
                  "aggressionIndex": 7,
                  "locationSector": "Verdant",
                  "latitude": 1.0,
                  "longitude": 1.0
                }
                """);

        String updateBody = """
                {
                  "codeName": "Roy Harper",
                  "mirakuruSaturation": 20,
                  "aggressionIndex": 1,
                  "locationSector": "Safehouse",
                  "latitude": 1.0,
                  "longitude": 1.0,
                  "status": "NEUTRALIZED"
                }
                """;

        mockMvc.perform(put("/api/afectados/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("NEUTRALIZED")))
                .andExpect(jsonPath("$.threatLevel", is("LOW")))
                .andExpect(jsonPath("$.priorityScore", is(0)));
    }

    @Test
    void returnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/afectados/{id}", "11111111-1111-1111-1111-111111111111"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void rejectsInvalidSaturationWithValidationError() throws Exception {
        String invalidBody = """
                {
                  "codeName": "Bad Data",
                  "mirakuruSaturation": 250,
                  "aggressionIndex": 9,
                  "locationSector": "Glades",
                  "latitude": 0.0,
                  "longitude": 0.0
                }
                """;

        mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("mirakuruSaturation")));
    }

    @Test
    void rejectsDuplicateCodeNameWithConflict() throws Exception {
        String body = """
                {
                  "codeName": "Unique Name",
                  "mirakuruSaturation": 50,
                  "aggressionIndex": 5,
                  "locationSector": "Glades",
                  "latitude": 0.0,
                  "longitude": 0.0
                }
                """;

        mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    private String registerAndReturnId(String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/afectados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asText();
    }
}
