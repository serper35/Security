package ru.t1.Order.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.t1.Order.dto.OrderDTO;
import ru.t1.Order.dto.UserDTO;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO user);

    UserDTO updateUser(UserDTO user);

    UserDTO getUser(int id);

    void deleteUser(int id);

    List<OrderDTO> getOrders(int id);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
