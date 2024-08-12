package ru.t1.Order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.Order.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByName(String name);
}
