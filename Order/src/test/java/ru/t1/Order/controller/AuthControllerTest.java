package ru.t1.Order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserService userService;

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
                        .content(objectMapper.writeValueAsString(request)))  // Преобразуйте объект напрямую
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegisterConflict() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        when(userService.createUser(any(UserDTO.class))).thenThrow(new UserAlreadyExistException("User already exist"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Используйте ObjectMapper
                .andExpect(status().isConflict());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Создаем тестовые данные
        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");
        UserDetails userDetails = BDDMockito.mock(UserDetails.class);

        // Настраиваем моки
        BDDMockito.given(userService.loadUserByUsername(anyString())).willReturn(userDetails);
        BDDMockito.given(userDetails.getPassword()).willReturn("encodedPassword");
        BDDMockito.given(jwtUtil.generateToken(any(UserDetails.class))).willReturn("jwtToken");

        // Выполняем запрос
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", \"password\":\"test\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("jwtToken")); // предположим, ваш JwtResponse содержит поле "token"
    }

//    @Test
//    public void testLoginSuccess() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        LoginRequest loginRequest = new LoginRequest("username", "password");
//
//        // Зашифруйте пароль
//        String rawPassword = "password";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        // Проверьте, что encodedPassword не равен null
//        if (encodedPassword == null) {
//            throw new IllegalStateException("Encoded password should not be null");
//        }
//
//        // Создайте объект UserDetails с зашифрованным паролем
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername("username")
//                .password(encodedPassword)  // Убедитесь, что encodedPassword не null
//                .authorities("ROLE_USER")
//                .build();
//
//        // Мокируйте методы
//        when(userService.loadUserByUsername("username")).thenReturn(userDetails);
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
//
//        // Выполняем запрос
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("token"));
//    }



//    @Test
//    public void testLoginSuccess() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        LoginRequest loginRequest = new LoginRequest("username", "password");
//
//        // Зашифруйте пароль
//        String encodedPassword = passwordEncoder.encode("password");  // Создайте зашифрованный пароль
//
//        // Создайте объект UserDetails с зашифрованным паролем
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername("username")
//                .password(encodedPassword)
//                .authorities("ROLE_USER")  // Убедитесь, что роли соответствуют вашим требованиям
//                .build();
//
//        // Мокируйте методы
//        when(userService.loadUserByUsername("username")).thenReturn(userDetails);
//        when(passwordEncoder.matches("password", encodedPassword)).thenReturn(true);  // Проверка пароля
//        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
//
//        // Выполняем запрос
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("token"));
//    }



//
//    @Test
//    public void testLoginSuccess() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        LoginRequest loginRequest = new LoginRequest("username", "password");
//        User user = new User();
//        user.setName("name");
//        user.setPassword("pass");
//        user.setRole(Role.ADMIN);
//
//        String authority = "ROLE_" + user.getRole().name();
//
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername(user.getName())
//                .password(user.getPassword())
//                .authorities(authority)
//                .build();;
//        when(userService.loadUserByUsername("username")).thenReturn(userDetails);
//        when(passwordEncoder.matches("password", "password")).thenReturn(true);
//        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))  // Используйте ObjectMapper
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("token"));
//    }

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

