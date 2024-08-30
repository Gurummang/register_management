package GASB.register_management.service.register;

import GASB.register_management.dto.register.OrgRequest;
import GASB.register_management.dto.register.OrgResponse;

import java.util.List;

public interface OrgService {
    OrgResponse registerOrg(OrgRequest orgRequest);
    OrgResponse modifyOrg(OrgRequest orgRequest);
    OrgResponse deleteOrg(Integer id);
    List<OrgResponse> getOrgList();
}
