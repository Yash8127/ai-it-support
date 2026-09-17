package com.yaswanth.itsupport.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yaswanth.itsupport.dto.NotificationResponse;
import com.yaswanth.itsupport.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService =
                notificationService;
    }

    @GetMapping
    public List<NotificationResponse>
    getMyNotifications() {

        return notificationService
                .getMyNotifications();
    }

    @GetMapping("/unread")
    public List<NotificationResponse>
    getMyUnreadNotifications() {

        return notificationService
                .getMyUnreadNotifications();
    }

    @GetMapping("/unread/count")
    public long getUnreadCount() {

        return notificationService
                .getUnreadCount();
    }
    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id) {

        notificationService.markAsRead(id);
    }
}