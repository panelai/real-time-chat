package swd392.resource.message;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swd392.enums.StatusNotification;
import swd392.model.dto.State;
import swd392.model.dto.message.MessageDTO;
import swd392.model.entity.message.Message;
import swd392.service.message.MessageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/messages")
public class MessageResource {
    private final MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Message> send(@RequestBody MessageDTO messageDTO) throws IOException {

        State<Message, String> sendState = messageService.send(messageDTO);
        if(sendState.getStatus().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(sendState.getValue());
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, sendState.getError());
            return ResponseEntity.of(problemDetail).build();
        }
    }
}
