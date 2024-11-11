package swd392.service.notification;

import swd392.model.dto.State;
import swd392.model.dto.message.MessageDTO;

import java.util.List;

public interface MessageChangeNotifier {
    State<Void, String> send(MessageDTO messageDTO, List<String> userToNotify);
}
