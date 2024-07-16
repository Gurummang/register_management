package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.OrgSaasConfig;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.OrgSaasConfigRepository;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.SaasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class OrgSaasServiceImple implements OrgSaasService {

    @Autowired
    private OrgSaasRepository orgSaasRepository;
    @Autowired
    private OrgSaasConfigRepository orgSaasConfigRepository;

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        return null;
    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
        return null;
    }

    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        return null;
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList() {
        return List.of();
    }
}
