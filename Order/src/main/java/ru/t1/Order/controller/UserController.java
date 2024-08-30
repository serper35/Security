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
import ru.t1.Order.exception.UserNotFoundException;
import ru.t1.Order.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Update user information",
            description = "Updates the user information. Users can only update their own information.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User details to be updated",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information successfully updated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: You can only update your own information"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO user, Authentication authentication) {
        try {
            String authName = authentication.getName();
            UserDTO updatedUser = userService.updateUser(user);

            if (authName.equals(updatedUser.getName())) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user information by ID. Users can only access their own information unless they are an 'ADMIN'.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the user to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: You can only access your own information or be an ADMIN"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<UserDTO> getUser(@PathVariable int id, Authentication authentication) {
        try {
            String authName = authentication.getName();
            UserDTO user = userService.getUser(id);

            if (authentication.getAuthorities().stream().anyMatch(
                    authority -> authority.getAuthority().equals("ROLE_ADMIN")) ||
                    user.getName().equals(authName)) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user by ID. Users can only delete their own account or if they are an 'ADMIN'.",
            parameters = {
                    @Parameter(name = "id", description = "ID of the user to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully deleted"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: You can only delete your own account or be an ADMIN"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<Void> deleteUser(@PathVariable int id, Authentication authentication) {
        try {
            String authName = authentication.getName();
            UserDTO user = userService.getUser(id);

            if (authentication.getAuthorities().stream().anyMatch(
                    authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                    || user.getName().equals(authName)) {
                userService.deleteUser(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/orders/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get orders by user ID",
            description = "Retrieves a list of orders for a specific user ID. Users can only access their own orders unless they are an 'ADMIN'.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user whose orders are to be retrieved", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden: You can only access your own orders or be an ADMIN"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable int userId, Authentication authentication) {
        try {
            String authName = authentication.getName();
            UserDTO user = userService.getUser(userId);

            if (authentication.getAuthorities().stream().anyMatch(
                    authority -> authority.getAuthority().equals("ROLE_ADMIN")) ||
                    user.getName().equals(authName)) {
                List<OrderDTO> orders = userService.getOrders(userId);
                return ResponseEntity.ok(orders);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
