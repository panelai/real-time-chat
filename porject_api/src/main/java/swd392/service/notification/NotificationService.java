package swd392.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import swd392.enums.NotificationEventName;
import swd392.model.entity.auth.Account;
import swd392.repository.auth.AccountRepository;

import java.io.IOException;
import java.util.*;


@Slf4j
@Service
public class NotificationService {

    private final AccountRepository accountRepository;

    public NotificationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Map<String, SseEmitter> emitters = new HashMap<>();

    @Scheduled(fixedRate = 5000)
    public void heartBeat() throws IOException {
        for (Map.Entry<String, SseEmitter> sseEmitter : emitters.entrySet()) {
            try {
                sseEmitter.getValue().send(SseEmitter.event()
                        .name("heartbeat")
                        .id(sseEmitter.getKey())
                        .data("Check heartbeat..."));
//                this.usersApplicationService.updatePresence(
//                        new UserPublicId(UUID.fromString(sseEmitter.getKey())));
            } catch (IllegalStateException e) {
                log.info("remove this one from the map {}", sseEmitter.getKey());
                this.emitters.remove(sseEmitter.getKey());
            }
        }
    }

    public SseEmitter addEmitter(String id) {
        Optional<Account> account = this.accountRepository.findById(id);
        if (account.isPresent()) {
            log.info("new Emitter added to the list : {}", account.get().getUsername());
            SseEmitter newEmitter = new SseEmitter(60000L);
            try {
                String userId = account.get().getId();
                newEmitter.send(SseEmitter.event()
                        .id(userId)
                        .data("Starting connection..."));
                this.emitters.put(userId, newEmitter);
                return newEmitter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void sendMessage(Object messageDTO, List<String> usersToNotify, NotificationEventName notificationEventName) {
        for (String userId : usersToNotify) {
            if (this.emitters.containsKey(userId)) {
                log.info("located userpublicid, let send him message : {}", userId);
                SseEmitter sseEmitter = this.emitters.get(userId);
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name(notificationEventName.value)
                            .id(userId)
                            .data(messageDTO));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
