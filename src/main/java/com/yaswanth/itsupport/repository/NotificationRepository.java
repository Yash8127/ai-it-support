package com.yaswanth.itsupport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yaswanth.itsupport.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserUsernameOrderByCreatedAtDesc(
            String username
    );

    List<Notification> findByUserUsernameAndIsReadFalseOrderByCreatedAtDesc(
            String username
    );

    long countByUserUsernameAndIsReadFalse(
            String username
    );
    Optional<Notification> findByIdAndUserUsername(
            Long id,
            String username
    );
}