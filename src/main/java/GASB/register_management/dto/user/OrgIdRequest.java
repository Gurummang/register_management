package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class OrgIdRequest {
    private long orgId;

    public OrgIdRequest(long orgId){
        this.orgId = orgId;
    }
}