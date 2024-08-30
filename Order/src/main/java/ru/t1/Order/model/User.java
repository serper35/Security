package ru.t1.Order.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "User entity representing a system user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1")
    private int id;

    @Schema(description = "Username of the user", example = "Vasya")
    private String name;

    @Schema(description = "Password of the user", example = "password")
    private String password;

    @Schema(description = "Email address of the user", example = "vasya@gmail.com")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Schema(description = "List of orders associated with the user")
    @ArraySchema(schema = @Schema(implementation = Order.class))
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Role of the user", example = "USER")
    private Role role;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", orders=" + orders +
                '}';
    }
}
