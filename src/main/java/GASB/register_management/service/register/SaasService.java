package GASB.register_management.service.register;

import GASB.register_management.dto.register.SaasRequest;
import GASB.register_management.dto.register.SaasResponse;

import java.util.List;

public interface SaasService {
    SaasResponse registerSaas(SaasRequest saasRequest);
    SaasResponse modifySaas(SaasRequest saasRequest);
    SaasResponse deleteSaas(Integer id);
    List<SaasResponse> getSaasList();
}
