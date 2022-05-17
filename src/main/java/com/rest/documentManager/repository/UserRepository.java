package com.rest.documentManager.repository;

import com.rest.documentManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailReturnUser(String email);

    @Query(value = "SELECT u FROM User u WHERE u.id = :idUser")
    Optional<User> findByIdReturnUser(Long idUser);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findByEmail (String email);

    @Query(value = "SELECT u FROM User u")
    List<Object> findAllUsers();

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailOrNot(String email);
}
