package GASB.register_management.service;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import java.util.List;

public interface OrgSaasService {

    OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest);
    OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest);
    OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest);
    List<OrgSaasResponse> getOrgSaasList();
}
