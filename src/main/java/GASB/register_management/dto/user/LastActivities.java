package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LastActivities {
    private int undormant;
    private int dormanting;
    private int dormant;
}
