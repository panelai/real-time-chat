package swd392.resource.conversation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swd392.enums.StatusNotification;
import swd392.model.dto.State;
import swd392.model.dto.conversation.ConversationDTO;
import swd392.model.dto.conversation.ConversationToCreateDTO;
import swd392.service.conversation.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationResource {
    private final ConversationService conversationService;

    public ConversationResource(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    ResponseEntity<List<ConversationDTO>> getAll() {
        List<ConversationDTO> conversationDTOs = conversationService.getAllByConnectedUser();
        return ResponseEntity.ok(conversationDTOs);
    }

    @GetMapping("/get-one-by-conversation-id")
    ResponseEntity<ConversationDTO> getOneByConversationId(@RequestParam String conversationId) {
        ConversationDTO conversationDTO = conversationService.getOneByConversationId(conversationId);
        if (conversationDTO != null) {
            return ResponseEntity.ok(conversationDTO);
        } else {
            ProblemDetail problemDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not able to find this conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @PostMapping
    ResponseEntity<ConversationDTO> create(@RequestBody ConversationToCreateDTO conversationToCreate) {
        State<ConversationDTO, String> conversationState = conversationService.create(conversationToCreate);
        if (conversationState.getStatus().equals(StatusNotification.OK)) {
            ConversationDTO conversationDTO = conversationState.getValue();
            return ResponseEntity.ok(conversationDTO);
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not allowed to create conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }
}
