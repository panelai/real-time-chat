package swd392.resource.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swd392.security.SecurityUtil;
import swd392.service.notification.NotificationService;

@RestController
@RequestMapping("/api/sse")
public class NotificationResource {
    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        return notificationService.addEmitter(SecurityUtil.getCurrentUserLogin());
    }
}
