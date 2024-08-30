package ru.t1.Order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.t1.Order.dto.OrderDTO;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.exception.UserAlreadyExistException;
import ru.t1.Order.exception.UserNotFoundException;
import ru.t1.Order.service.OrderService;
import ru.t1.Order.service.UserService;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Management", description = "Operations related to managing orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order. Only users with the 'USER' role can create orders.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details to be created",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OrderDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order successfully created"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO order) {
        try {
            OrderDTO createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing order",
            description = "Updates an existing order. Only users with the 'ADMIN' role can update orders.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details to be updated",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = OrderDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody OrderDTO order) {
        try {
            OrderDTO updatedOrder = orderService.updateOrder(order);
            return ResponseEntity.ok(updatedOrder);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves an order by its ID. Accessible by users with 'USER' or 'ADMIN' roles. Users can only access their own orders unless they have 'ADMIN' role.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the order to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<OrderDTO> getOrder(@PathVariable int id, Authentication authentication) {
        try {
            OrderDTO order = orderService.getOrder(id);
            UserDTO user = userService.getUser(order.getUserId());
            String authName = authentication.getName();

            if (authName.equals(user.getName()) || authentication.getAuthorities().stream().anyMatch(
                    authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete an order",
            description = "Deletes an order by its ID. Only users with the 'ADMIN' role can delete orders.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the order to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
