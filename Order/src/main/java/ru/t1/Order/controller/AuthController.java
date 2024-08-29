package ru.t1.Order.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.t1.Order.dto.JwtResponse;
import ru.t1.Order.dto.LoginRequest;
import ru.t1.Order.dto.RegisterRequest;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.exception.UserAlreadyExistException;
import ru.t1.Order.model.Role;
import ru.t1.Order.service.UserService;
import ru.t1.Order.util.JwtTokenUtil;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
@Data
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest registerRequest){
        try {
            UserDTO user = new UserDTO();
            user.setName(registerRequest.getName());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setOrders(new ArrayList<>());
            user.setRole(Role.USER);

            UserDTO createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse>  login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getName());

            if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(new JwtResponse(token));
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
