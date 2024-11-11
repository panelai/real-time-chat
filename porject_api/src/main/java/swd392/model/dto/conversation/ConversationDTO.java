package swd392.model.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swd392.enums.ConversationType;
import swd392.model.dto.account.MemberDTO;
import swd392.model.dto.message.MessageDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String title;

    private ConversationType type;

    private String groupAdmin;

    private List<String> messageIds;

    private List<MessageDTO> messages;

    private List<String> memberIds;

    private List<MemberDTO> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
