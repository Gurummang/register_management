package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserTotalDto {

    private int total;
    private int dormantTotal;
    private int dlpTotal;
    private int malwareTotal;
}
