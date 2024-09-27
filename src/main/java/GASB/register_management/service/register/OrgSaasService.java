package GASB.register_management.service.register;

import GASB.register_management.dto.register.OrgSaasRequest;
import GASB.register_management.dto.register.OrgSaasResponse;
import GASB.register_management.dto.register.ValidateDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrgSaasService {
    OrgSaasResponse slackValid(OrgSaasRequest orgSaasRequest, ValidateDto validateDto);
    OrgSaasResponse getUrl(Integer saasId);
    OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest);
    OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest);
    OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest);
    List<OrgSaasResponse> getOrgSaasList(Integer orgId);
    void updateOrgSaasGD(List<String[]> drives, String accessToken);
    void updateOrgSaasMS(List<String[]> drives, String accessToken);
}
