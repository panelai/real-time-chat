package swd392.model.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String username;
}
