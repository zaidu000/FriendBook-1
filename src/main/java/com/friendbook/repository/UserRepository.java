package com.friendbook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.friendbook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> searchByUsername(@Param("keyword") String keyword);

    Optional<User> findByUsername(String username);
    
    List<User> findByUsernameContainingIgnoreCase(String keyword);
}
