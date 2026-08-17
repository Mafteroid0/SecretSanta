package ru.mafteroid.secretsanta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mafteroid.secretsanta.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameIgnoreCase(String username);
    //Optional<User> findById(String email);
    boolean existsByUsernameIgnoreCase(String username);
}
