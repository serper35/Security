package ru.t1.Order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import ru.t1.Order.model.User;
import ru.t1.Order.util.JwtTokenUtil;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

//    @Test
//    public void testSuccessfulAuthentication() throws Exception {
//        String username = "username";
//        String password = "password";
//        UserDetails userDetails = new User(username, password, Collections.emptyList());
//        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
//        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
//
//        mockMvc.perform(post("/auth/login")
//                        .param("username", username)
//                        .param("password", password))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Authorization", "Bearer token"));
//    }

    @Test
    public void testAuthenticationFailure() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("username", "username")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
}

