package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.WorkspaceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrgSaasServiceImple implements OrgSaasService {

    @Autowired
    private OrgSaasRepository orgSaasRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        OrgSaas orgSaas = new OrgSaas();
        Workspace workSpace = new Workspace();

        // config
        workSpace.setWorkspace_name(orgSaasRequest.getWorkspace_name());
//      workSpace.setAlias(orgSaasRequest.getAlias());  // 용도가 애매해
        workSpace.setSaas_admin_email(orgSaasRequest.getSaas_admin_email());
        workSpace.setWebhook(orgSaasRequest.getWebhook_url());
        workSpace.setToken(orgSaasRequest.getToken());
        Workspace saveWorkSpace = workspaceRepository.save(workSpace);
        // OrgSaas
        orgSaas.setOrg_id(orgSaasRequest.getOrg_id());;
        orgSaas.setSaas_id(orgSaasRequest.getSaas_id());
        orgSaas.setConfig(saveWorkSpace.getId());
        orgSaas.setSpace_id(String.valueOf(orgSaasRequest.getSpace_id()));  // Setter가 id라 int로 인식한듯?
        OrgSaas saveOrgSaas = orgSaasRepository.save(orgSaas);

        return new OrgSaasResponse(saveWorkSpace.getId(), saveWorkSpace.getWorkspace_name());

    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {

//        OrgSaasConfig orgSaasConfig = new OrgSaasConfig();
//
//        orgSaasConfig.setSaas_admin_email(orgSaasRequest.getSaas_admin_email());
//        orgSaasConfig.setApi_key(orgSaasRequest.getApi_key());
//        orgSaasConfig.setSaas_alias(orgSaasRequest.getSaas_alias());
//        orgSaasConfig.setRegister_date(Timestamp.valueOf(LocalDateTime.now()));
//
//        OrgSaasConfig updateOrgSaasConfig = orgSaasConfigRepository.save(orgSaasConfig);
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

//    @Override
//    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
//        Optional<OrgSaasConfig> optionalOrgSaasConfig = orgSaasConfigRepository.findById(Long.valueOf(orgSaasRequest.getOrg_saas_id()));
//        // 솔루션 접속 시, org_id는 결정됨
//        // 여기서는 org_id는 1이라고 가정하고 진행
//        // FE는 알고있는 org_id+saas_id를 POST
//        if(optionalOrgSaasConfig.isPresent()) {
//            OrgSaasConfig orgSaasConfig = optionalOrgSaasConfig.get();
//
//            if(orgSaasConfig.getId().equals(orgSaasRequest.getOrg_saas_id()) && orgSaasConfig.getSaas_admin_email().equals(orgSaasRequest.getSaas_admin_email())) {
//                orgSaasConfig.setSaas_admin_email(orgSaasRequest.getSaas_admin_email());
//                orgSaasConfig.setApi_key(orgSaasRequest.getApi_key());
//                orgSaasConfig.setSaas_alias(orgSaasRequest.getSaas_alias());
//
//                OrgSaasConfig updateOrgSaasConfig = orgSaasConfigRepository.save(orgSaasConfig);
//                OrgSaas updateOrgSaas = new OrgSaas();
//
//                return new OrgSaasResponse("success", updateOrgSaasConfig.getId(), updateOrgSaas.getStatus(), orgSaasConfig.getWebhook_url(), orgSaasConfig.getSaas_admin_email(), orgSaasConfig.getSaas_alias());
//            }
//            else {
//                return new OrgSaasResponse("failure", "ID & Email Unmatched!");
//            }
//        }
//        else {
//            return new OrgSaasResponse("failure", "OrgSaas not found for ID: " + orgSaasRequest.getOrg_saas_id());
//        }
//    }
//
//    @Override
//    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
//        Optional<OrgSaasConfig> optionalOrgSaasConfig = orgSaasConfigRepository.findById(Long.valueOf(orgSaasRequest.getOrg_saas_id()));
//
//        if (optionalOrgSaasConfig.isPresent()) {
//            OrgSaasConfig orgSaasConfig = optionalOrgSaasConfig.get();
//
//            if(orgSaasConfig.getId().equals(orgSaasRequest.getOrg_saas_id()) && orgSaasConfig.getSaas_admin_email().equals(orgSaasRequest.getSaas_admin_email())) {
//                orgSaasConfigRepository.deleteById(Long.valueOf(orgSaasRequest.getOrg_saas_id()));
//                return new OrgSaasResponse("success", orgSaasRequest.getOrg_saas_id(), orgSaasRequest.getSaas_admin_email());
//            }
//            else {
//                return new OrgSaasResponse("failure", "OrgSaas not found for ID: " + orgSaasRequest.getOrg_saas_id());
//            }
//        }
//        else {
//            return new OrgSaasResponse("failure", "OrgSaas not found for ID: " + orgSaasRequest.getOrg_saas_id());
//        }
//    }
//
//    @Override
//    public List<OrgSaasResponse> getOrgSaasList() {
//        return orgSaasRepository.findAll().stream()
//                .map(orgSaas -> {
//                    Optional<OrgSaasConfig> configOptional = orgSaasConfigRepository.findById(Long.valueOf(orgSaas.getConfig_file()));
//                    OrgSaasConfig config = configOptional.orElse(new OrgSaasConfig());
//
//                    Optional<Saas> saasOptional = saasRepository.findById(orgSaas.getSaas_id());
//                    Saas saas = saasOptional.orElse(new Saas());
//
//                    return new OrgSaasResponse(
//                            "success",
//                            orgSaas.getId(),
//                            orgSaas.getStatus(),
//                            config.getWebhook_url(),
//                            config.getSaas_admin_email(),
//                            config.getSaas_alias(),
//                            config.getRegister_date()
//                    );
//                })
//                .collect(Collectors.toList());
//    }

}
