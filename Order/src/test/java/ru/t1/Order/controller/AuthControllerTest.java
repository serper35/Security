package ru.t1.Order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.t1.Order.dto.LoginRequest;
import ru.t1.Order.dto.RegisterRequest;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.exception.UserAlreadyExistException;
import ru.t1.Order.model.Role;
import ru.t1.Order.model.User;
import ru.t1.Order.service.UserService;
import ru.t1.Order.service.impl.UserServiceImpl;
import ru.t1.Order.util.JwtTokenUtil;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtTokenUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        UserDTO userDTO = new UserDTO();
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegisterConflict() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        when(userService.createUser(any(UserDTO.class))).thenThrow(new UserAlreadyExistException("User already exist"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testLoginBadCredentials() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest = new LoginRequest("username", "wrongpassword");
        when(userService.loadUserByUsername("username")).thenThrow(new UsernameNotFoundException(""));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

