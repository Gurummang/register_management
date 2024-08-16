package GASB.register_management.service;

import GASB.register_management.dto.OrgRequest;
import GASB.register_management.dto.OrgResponse;

import java.util.List;

public interface OrgService {
    OrgResponse registerOrg(OrgRequest orgRequest);
    OrgResponse modifyOrg(OrgRequest orgRequest);
    OrgResponse deleteOrg(Integer id);
    List<OrgResponse> getOrgList();
}
