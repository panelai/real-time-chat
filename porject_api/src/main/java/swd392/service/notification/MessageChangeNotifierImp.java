package swd392.service.notification;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import swd392.enums.NotificationEventName;
import swd392.model.dto.State;
import swd392.model.dto.message.MessageDTO;
import swd392.model.record.MessageWithUsers;

import java.util.List;

@Service
public class MessageChangeNotifierImp implements MessageChangeNotifier {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MessageChangeNotifierImp(ApplicationEventPublisher applicationEventPublisher, NotificationService notificationService) {
        this.notificationService = notificationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public State<Void, String> send(MessageDTO messageDTO, List<String> userToNotify) {
        MessageWithUsers messageWithUsers = new MessageWithUsers(messageDTO, userToNotify);
        applicationEventPublisher.publishEvent(messageWithUsers);

        return State.<Void, String>builder().forSuccess();
    }

    @EventListener
    public void handleNewMessage(MessageWithUsers messageWithUsers) {
        notificationService.sendMessage(messageWithUsers.messageDTO(), messageWithUsers.userToNotify(),
                NotificationEventName.NEW_MESSAGE);
    }

//    @Override
//    public State<Void, String> delete(ConversationPublicId conversationPublicId,
//                                      List<UserPublicId> userToNotify) {
//        ConversationIdWithUsers conversationIdWithUsers = new ConversationIdWithUsers(conversationPublicId, userToNotify);
//        applicationEventPublisher.publishEvent(conversationIdWithUsers);
//        return State.<Void, String>builder().forSuccess();
//    }

//    @Override
//    public State<Void, String> view(ConversationViewedForNotification conversationViewedForNotification, List<UserPublicId> usersToNotify) {
//        MessageIdWithUsers messageIdWithUsers = new MessageIdWithUsers(conversationViewedForNotification, usersToNotify);
//        applicationEventPublisher.publishEvent(messageIdWithUsers);
//        return State.<Void, String>builder().forSuccess();
//    }

//    @EventListener
//    public void handleDeleteConversation(ConversationIdWithUsers conversationIdWithUsers) {
//        notificationService.sendMessage(conversationIdWithUsers.conversationPublicId().value(),
//                conversationIdWithUsers.users(), NotificationEventName.DELETE_CONVERSATION);
//    }

//    @EventListener
//    public void handleView(MessageIdWithUsers messageIdWithUsers) {
//        notificationService.sendMessage(messageIdWithUsers.conversationViewedForNotification(),
//                messageIdWithUsers.usersToNotify(), NotificationEventName.VIEWS_MESSAGES);
//    }
}
