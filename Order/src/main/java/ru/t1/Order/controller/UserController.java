package ru.t1.Order.controller;

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

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping
    @PreAuthorize("hasRole('USER')")
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
