package ru.t1.Order.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.t1.Order.dto.OrderDTO;
import ru.t1.Order.dto.UserDTO;
import ru.t1.Order.exception.UserAlreadyExistException;
import ru.t1.Order.exception.UserNotFoundException;
import ru.t1.Order.mapper.Mapper;
import ru.t1.Order.model.Order;
import ru.t1.Order.model.User;
import ru.t1.Order.repository.OrderRepository;
import ru.t1.Order.repository.UserRepository;
import ru.t1.Order.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private Mapper mapper;

    public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByName(userDTO.getName())) {
            throw new UserAlreadyExistException("User already exist");
        }
        User user = mapper.convertToEntity(userDTO);
        user.setOrders(new ArrayList<>());
        User savedUser = userRepository.save(user);
        return mapper.convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found exception"));

        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setOrders(userDTO.getOrders().stream()
                .map(mapper::convertOrderToEntity)
                .collect(Collectors.toList()));

        User updatedUser = userRepository.save(user);
        return mapper.convertToDTO(updatedUser);
    }

    @Override
    public UserDTO getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found exception"));
        return mapper.convertToDTO(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found exception"));
        userRepository.deleteById(id);
    }

    public List<OrderDTO> getOrders(int id) {
        List<Order> orders = orderRepository.findByUser_Id(id);
        return orders.stream()
                .map(mapper::convertOrderToDTO)
                .collect(Collectors.toList());
    }


    public User findUserByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() -> new  UserNotFoundException("User not found exception"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = findUserByUsername(username);
        String authority = "ROLE_" + user.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getName())
                .password(user.getPassword())
                .authorities(authority)
                .build();
    }
}
