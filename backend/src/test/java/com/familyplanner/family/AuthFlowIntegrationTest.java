package com.familyplanner.family;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class AuthFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String register(String email) throws Exception {
        String body = """
                {"familyName":"Smiths","name":"Pat","email":"%s","password":"password123","color":"#112233"}
                """.formatted(email);
        String res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.member.role").value("PARENT"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(res).get("token").asText();
    }

    @Test
    void parentCanAddChildAndChildCannotAddMembers() throws Exception {
        String parentToken = register("pat@example.com");

        String child = """
                {"name":"Kid","email":"kid@example.com","password":"password123","role":"CHILD","color":"#00ff00"}
                """;
        mvc.perform(post("/api/family/members").header("Authorization", "Bearer " + parentToken)
                        .contentType(MediaType.APPLICATION_JSON).content(child))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/family").header("Authorization", "Bearer " + parentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members.length()").value(2));

        String login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"kid@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String childToken = json.readTree(login).get("token").asText();

        mvc.perform(post("/api/family/members").header("Authorization", "Bearer " + childToken)
                        .contentType(MediaType.APPLICATION_JSON).content(child.replace("kid@", "kid2@")))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsUnauthenticatedBadLoginAndDuplicateEmail() throws Exception {
        mvc.perform(get("/api/family")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());

        register("dup@example.com");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyName\":\"X\",\"name\":\"Y\",\"email\":\"DUP@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict());
    }
}
