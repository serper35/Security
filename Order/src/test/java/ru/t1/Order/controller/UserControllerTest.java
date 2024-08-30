package ru.t1.Order.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import ru.t1.Order.dto.OrderDTO;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.exception.UserNotFoundException;
import ru.t1.Order.service.impl.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void testUpdateUserSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");
        when(userService.updateUser(any(UserDTO.class))).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void testUpdateUserForbidden() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otherUser");
        when(userService.updateUser(any(UserDTO.class))).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUserNotFound() {
        UserDTO userDTO = new UserDTO();
        when(userService.updateUser(any(UserDTO.class))).thenThrow(UserNotFoundException.class);

        ResponseEntity<UserDTO> response = userController.updateUser(userDTO, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<UserDTO> response = userController.getUser(1, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void testGetUserForbidden() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otherUser");
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<UserDTO> response = userController.getUser(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetUserNotFound() {
        when(userService.getUser(anyInt())).thenThrow(UserNotFoundException.class);

        ResponseEntity<UserDTO> response = userController.getUser(1, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteUserSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        doNothing().when(userService).deleteUser(anyInt());
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<Void> response = userController.deleteUser(1, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteUserForbidden() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otherUser");
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<Void> response = userController.deleteUser(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testDeleteUserNotFound() {
        when(userService.getUser(anyInt())).thenThrow(UserNotFoundException.class);

        ResponseEntity<Void> response = userController.deleteUser(1, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrdersByUserIdSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");
        List<OrderDTO> orders = List.of(new OrderDTO());

        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(userService.getOrders(anyInt())).thenReturn(orders);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<List<OrderDTO>> response = userController.getOrdersByUserId(1, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    void testGetOrdersByUserIdForbidden() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otherUser");
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<List<OrderDTO>> response = userController.getOrdersByUserId(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetOrdersByUserIdNotFound() {
        when(userService.getUser(anyInt())).thenThrow(UserNotFoundException.class);

        ResponseEntity<List<OrderDTO>> response = userController.getOrdersByUserId(1, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
