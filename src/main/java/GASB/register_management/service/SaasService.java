package GASB.register_management.service;

import GASB.register_management.dto.SaasRequest;
import GASB.register_management.dto.SaasResponse;

import java.util.List;

public interface SaasService {

    SaasResponse registerSaas(SaasRequest saasRequest);
    SaasResponse modifySaas(SaasRequest saasRequest);
    SaasResponse deleteSaas(SaasRequest saasRequest);
    List<SaasResponse> getSaasList();
}
