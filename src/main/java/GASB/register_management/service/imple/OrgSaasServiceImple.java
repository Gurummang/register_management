package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.entity.Admin;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.AdminRepository;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.WorkspaceRepository;

import GASB.register_management.util.StartScan;
import GASB.register_management.util.validation.SlackTeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrgSaasServiceImple implements OrgSaasService {

    @Autowired
    private OrgSaasRepository orgSaasRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private SaasRepository saasRepository;
    @Autowired
    private SlackTeamInfo slackTeamInfo;
    @Autowired
    private StartScan startScan;
    @Autowired
    private AdminRepository adminRepository;

    public OrgSaasServiceImple(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }


    @Override
    public OrgSaasResponse slackValid(OrgSaasRequest orgSaasRequest) {
        Workspace workspace = new Workspace();

        String token = orgSaasRequest.getApiToken();

        try {
            slackTeamInfo.getTeamInfo(token);

            return new OrgSaasResponse(200, null, true, null, null);
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse(199, e.getMessage(), false, null, null);
        }
    }

    @Override
    public OrgSaasResponse getUrl(Integer saasId) {
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()) {
            Saas saas = saasOptional.get();

            System.out.println(saas.getSaasName());
            return new OrgSaasResponse( 200, null,
                    "https://back.grummang.com/webhook/"+saas.getSaasName()+ "/" + UUID.randomUUID());
        }else {
            return new OrgSaasResponse( 199, "Not found for ID", null);
        }
    }

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        OrgSaas orgSaas = new OrgSaas();
        Workspace workspace = new Workspace();
        Admin admin = new Admin();

        try {
            List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());
            // token validation
            String spaceName = slackInfo.get(0);
            String spaceId = slackInfo.get(1);
            workspace.setSpaceName(spaceName);
            orgSaas.setSpaceId(spaceId);
            // workspace_config
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken(orgSaasRequest.getApiToken());
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace registeredWorkspace = workspaceRepository.save(workspace);
            // org_saas
            orgSaas.setOrgId(orgSaasRequest.getOrgId());
            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setConfig(registeredWorkspace.getId());
            orgSaasRepository.save(orgSaas);

            //saasId -> saasName
            String saasName = saasRepository.findById(orgSaasRequest.getSaasId()).get().getSaasName();
            String adminEmail = adminRepository.findById(orgSaasRequest.getOrgId()).get().getEmail();
//            System.out.println(saasName);
//            System.out.println(adminEmail);
            try{
                startScan.postToScan(orgSaas.getSpaceId(), adminEmail, saasName);

                return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
            } catch (Exception e) {
                return new OrgSaasResponse(198, e.getMessage(), null, null);
            }

        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse( 199, "API token Invalid\n"+e.getMessage(),null, null);
        }
    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getId()));
        List<OrgSaas> orgSaasList = orgSaasRepository.findByConfig(orgSaasRequest.getId());
        Admin admin = new Admin();

        if (optionalWorkspace.isPresent() && !orgSaasList.isEmpty()) {
            Workspace workspace = optionalWorkspace.get();
            OrgSaas orgSaas = orgSaasList.get(0);

            try {
                // token validation
                if (orgSaasRequest.getApiToken() != null) {
                    List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());
                    String spaceName = slackInfo.get(0);
                    String spaceId = slackInfo.get(1);
                    workspace.setSpaceName(spaceName);
                    orgSaas.setSpaceId(spaceId);
                }

                // workspace_config
                if (orgSaasRequest.getAlias() != null) {
                    workspace.setAlias(orgSaasRequest.getAlias());
                }
                if (orgSaasRequest.getAdminEmail() != null) {
                    workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                }
                if (orgSaasRequest.getApiToken() != null) {
                    workspace.setApiToken(orgSaasRequest.getApiToken());
                }
                if (orgSaasRequest.getWebhookUrl() != null) {
                    workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                }

                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);

                // org_saas
                orgSaas.setConfig(registeredWorkspace.getId());
                orgSaasRepository.save(orgSaas);

                Integer orgId = orgSaas.getOrgId();
                Integer saasId = orgSaas.getSaasId();

                //saasId -> saasName
                String saasName = saasRepository.findById(saasId).get().getSaasName();
                String adminEmail = adminRepository.findById(orgId).get().getEmail();

                try{
                    startScan.postToScan(orgSaas.getSpaceId(), registeredWorkspace.getAdminEmail(), saasName);

                    return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
                } catch (Exception e) {
                    return new OrgSaasResponse(199, e.getMessage(), null, null);
                }

            } catch (IOException | InterruptedException e) {
                return new OrgSaasResponse(199, "API token Invalid\n" + e.getMessage(), null, null);
            }
        } else {
            return new OrgSaasResponse(199, "Not found for ID or no associated OrgSaas found", null);
        }
    }




    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getId()));

        if(optionalWorkspace.isPresent()) {
            Workspace workspace = optionalWorkspace.get();

            List<OrgSaas> orgSaasList = orgSaasRepository.findByConfig(orgSaasRequest.getId());
//            CASCADE로 처리
//            orgSaasRepository.deleteAll(orgSaasList);
            workspaceRepository.delete(workspace);

            return new OrgSaasResponse( 200, null, null);
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", null);
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        // 1. orgId로 org_saas 테이블에서 튜플 조회
        List<OrgSaas> orgSaasList = orgSaasRepository.findByOrgId(orgId);

        // 2. 조회된 orgSaas 데이터에서 config 값을 추출
        List<Integer> configIds = orgSaasList.stream()
                .map(OrgSaas::getConfig)
                .distinct()  // 중복 제거
                .collect(Collectors.toList());

        // 3. config 값을 사용하여 workspace_config 테이블에서 데이터 조회
        List<Workspace> workspaceList = workspaceRepository.findByIdIn(configIds);

        // 4. configId를 기준으로 Workspace 객체를 맵으로 변환
        Map<Integer, Workspace> workspaceMap = workspaceList.stream()
                .collect(Collectors.toMap(Workspace::getId, workspace -> workspace));

        // 5. 결과 리스트 생성
        return orgSaasList.stream().map(orgSaas -> {
            // Lookup workspace by configId
            Workspace workspace = workspaceMap.get(orgSaas.getConfig());

            Optional<Saas> saasOptional = saasRepository.findById(orgSaas.getSaasId());
            String saasName = saasOptional.map(Saas::getSaasName).orElse("Unknown");

            return new OrgSaasResponse(
                    workspace != null ? workspace.getId() : null,
                    saasName,
                    workspace != null ? workspace.getAlias() : null,
                    orgSaas.getStatus(),
                    workspace != null ? workspace.getAdminEmail() : null,
                    workspace != null ? workspace.getApiToken() : null,
                    workspace != null ? workspace.getWebhookUrl() : null,
                    workspace != null ? workspace.getRegisterDate() : null
            );
        }).collect(Collectors.toList());
    }

}
