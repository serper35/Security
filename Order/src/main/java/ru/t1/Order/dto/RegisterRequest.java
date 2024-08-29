package ru.t1.Order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String name;

    private String password;

    private String email;
}
