package ru.t1.Order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicEndpoints() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());

//        mockMvc.perform(get("/orders/some-order"))
//                .andExpect(status().isOk());
    }

    @Test
    public void testProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }
}
