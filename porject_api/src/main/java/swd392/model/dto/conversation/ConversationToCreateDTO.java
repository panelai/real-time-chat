package swd392.model.dto.conversation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import swd392.enums.ConversationType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationToCreateDTO {
    private String title;

    @NotNull
    private ConversationType type;

    @NotNull
    private List<String> memberIds;
}
