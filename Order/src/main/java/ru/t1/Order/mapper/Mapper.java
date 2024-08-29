package ru.t1.Order.mapper;

import org.springframework.stereotype.Component;
import ru.t1.Order.dto.OrderDTO;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.model.Order;
import ru.t1.Order.model.User;
import ru.t1.Order.repository.UserRepository;

import java.util.stream.Collectors;

@Component
public class Mapper {
    private UserRepository userRepository;

    public Mapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO convertToDTO(User user){
    UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setOrders(user.getOrders().stream()
                .map(this::convertOrderToDTO)
                .collect(Collectors.toList()));
        return dto;
}

    public User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setOrders(userDTO.getOrders().stream()
                .map(this::convertOrderToEntity)
                .collect(Collectors.toList()));
        return user;
    }

    public OrderDTO convertOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : 0);
        return dto;
    }

    public Order convertOrderToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setDescription(orderDTO.getDescription());
        order.setStatus(orderDTO.getStatus());
        if (orderDTO.getUserId() != 0) {
            User user = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setUser(user);
        }
        return order;
    }
}
