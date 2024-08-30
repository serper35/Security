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
import ru.t1.Order.exception.UserAlreadyExistException;
import ru.t1.Order.exception.UserNotFoundException;
import ru.t1.Order.service.impl.OrderServiceImpl;
import ru.t1.Order.service.impl.UserServiceImpl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    @Test
    void testCreateOrderSuccess() {
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void testCreateOrderConflict() {
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.createOrder(any(OrderDTO.class))).thenThrow(UserAlreadyExistException.class);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateOrderSuccess() {
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.updateOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void testUpdateOrderNotFound() {
        OrderDTO orderDTO = new OrderDTO();
        when(orderService.updateOrder(any(OrderDTO.class))).thenThrow(UserNotFoundException.class);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(orderDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrderSuccessUser() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1);
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");

        when(orderService.getOrder(anyInt())).thenReturn(orderDTO);
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<OrderDTO> response = orderController.getOrder(1, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void testGetOrderForbidden() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1);
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otherUser");

        when(orderService.getOrder(anyInt())).thenReturn(orderDTO);
        when(userService.getUser(anyInt())).thenReturn(userDTO);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<OrderDTO> response = orderController.getOrder(1, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetOrderNotFound() {
        when(orderService.getOrder(anyInt())).thenThrow(UserNotFoundException.class);

        ResponseEntity<OrderDTO> response = orderController.getOrder(1, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteOrderSuccess() {
        doNothing().when(orderService).deleteOrder(anyInt());

        ResponseEntity<Void> response = orderController.deleteOrder(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteOrderNotFound() {
        doThrow(UserNotFoundException.class).when(orderService).deleteOrder(anyInt());

        ResponseEntity<Void> response = orderController.deleteOrder(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
