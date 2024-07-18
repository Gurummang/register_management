package GASB.register_management.repository;

import GASB.register_management.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
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