package com.yaswanth.itsupport.service;

import com.yaswanth.itsupport.dto.NotificationResponse;
import com.yaswanth.itsupport.entity.Notification;
import com.yaswanth.itsupport.entity.User;
import com.yaswanth.itsupport.repository.NotificationRepository;
import com.yaswanth.itsupport.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(
            String username,
            String message,
            String type) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(
                LocalDateTime.now());

        notificationRepository.save(
                notification);
    }

    public List<NotificationResponse>
    getMyNotifications() {

        String username =
                getCurrentUsername();

        return notificationRepository
                .findByUserUsernameOrderByCreatedAtDesc(
                        username)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<NotificationResponse>
    getMyUnreadNotifications() {

        String username =
                getCurrentUsername();

        return notificationRepository
                .findByUserUsernameAndIsReadFalseOrderByCreatedAtDesc(
                        username)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public long getUnreadCount() {

        String username =
                getCurrentUsername();

        return notificationRepository
                .countByUserUsernameAndIsReadFalse(
                        username);
    }
    
    public void markAsRead(Long notificationId) {

        String username = getCurrentUsername();

        Notification notification =
                notificationRepository
                        .findByIdAndUserUsername(
                                notificationId,
                                username
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    private NotificationResponse
    convertToResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}