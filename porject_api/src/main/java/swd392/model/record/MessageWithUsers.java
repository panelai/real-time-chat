package swd392.model.record;

import swd392.model.dto.message.MessageDTO;

import java.util.List;

public record MessageWithUsers(MessageDTO messageDTO, List<String> userToNotify) {
}

