package com.example.userauthservice.repos;

import com.example.userauthservice.models.Session;
import com.example.userauthservice.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepo extends JpaRepository<Session, Long> {
    Optional<Session> findByTokenAndUserId(String token, Long userId);
    List<Session> findAllByUserIdAndState(Long user_id, State state);
}
